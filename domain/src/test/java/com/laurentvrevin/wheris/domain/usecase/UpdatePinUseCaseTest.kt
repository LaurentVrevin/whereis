package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UpdatePinUseCaseTest {
    private val original =
        Pin(
            PinId("edit"), GeoPoint(10.0, 20.0), SystemCategoryIds.CAR, 7f, 30.0,
            1000L, 2000L, "Original", "Original note", false, PhotoReference("photo-a"),
        )
    private val repository = FakeRepository(original)
    private val updatePin = UpdatePinUseCase(repository) { 5000L }

    private fun update(photo: PinPhotoChange = PinPhotoChange.Keep) =
        PinUpdate(original.id, original.categoryId, original.name, original.note, original.isFavorite, photo)

    @Test fun `text editing normalizes name and preserves useful note including indentation`() =
        runBlocking {
            val note = "  Note\n 🌲  "
            assertEquals(PinUpdateResult.SUCCESS, updatePin(update().copy(name = "  Café 東京  ", note = note)))
            assertEquals(original.copy(name = "Café 東京", note = note, updatedAtEpochMillis = 5000L), repository.pin)
        }

    @Test fun `blank name becomes null`() =
        runBlocking {
            updatePin(update().copy(name = " \t\n "))
            assertEquals(null, repository.pin!!.name)
        }

    @Test fun `blank note becomes null`() =
        runBlocking {
            updatePin(update().copy(note = " \t\n "))
            assertEquals(null, repository.pin!!.note)
        }

    @Test fun `optional null fields remain null`() =
        runBlocking {
            updatePin(update().copy(name = null, note = null))
            assertEquals(null, repository.pin!!.name)
            assertEquals(null, repository.pin!!.note)
        }

    @Test fun `category changes without geographic or creation changes`() =
        runBlocking {
            updatePin(update().copy(categoryId = SystemCategoryIds.PARKING))
            assertEquals(original.copy(categoryId = SystemCategoryIds.PARKING, updatedAtEpochMillis = 5000L), repository.pin)
        }

    @Test fun `favorite can be enabled and disabled`() =
        runBlocking {
            updatePin(update().copy(isFavorite = true))
            assertEquals(true, repository.pin!!.isFavorite)
            updatePin(update().copy(isFavorite = false))
            assertEquals(false, repository.pin!!.isFavorite)
        }

    @Test fun `keep retains exact photo and updates timestamp`() =
        runBlocking {
            updatePin(update())
            assertEquals(original.copy(updatedAtEpochMillis = 5000L), repository.pin)
            assertEquals(PinPhotoChange.Keep, repository.submitted!!.photoChange)
        }

    @Test fun `remove is explicit`() =
        runBlocking {
            updatePin(update(PinPhotoChange.Remove))
            assertEquals(original.copy(photoReference = null, updatedAtEpochMillis = 5000L), repository.pin)
        }

    @Test fun `replace carries a permanent neutral reference`() =
        runBlocking {
            val photo = PhotoReference("photo-b")
            updatePin(update(PinPhotoChange.Replace(photo)))
            assertEquals(original.copy(photoReference = photo, updatedAtEpochMillis = 5000L), repository.pin)
        }

    @Test fun `missing pin returns a result without creating anything`() =
        runBlocking {
            repository.pin = null
            assertEquals(PinUpdateResult.PIN_NOT_FOUND, updatePin(update()))
            assertEquals(null, repository.pin)
        }

    @Test fun `missing category result preserves the original`() =
        runBlocking {
            repository.result = PinUpdateResult.CATEGORY_NOT_FOUND
            assertEquals(PinUpdateResult.CATEGORY_NOT_FOUND, updatePin(update()))
            assertEquals(original, repository.pin)
        }

    @Test fun `all failures and committed pending cleanup results retain their meaning`() =
        runBlocking {
            for (result in PinUpdateResult.entries) {
                repository.result = result
                assertEquals(result, updatePin(update()))
            }
        }

    @Test fun `cancellation is propagated`() {
        repository.cancel = true
        assertThrows(CancellationException::class.java) { runBlocking { updatePin(update()) } }
    }

    @Test fun `compatibility editor delegates keep category and favorite to canonical mutation`() =
        runBlocking {
            val details = UpdatePinDetailsUseCase(repository) { 6000L }
            assertEquals(PinUpdateResult.SUCCESS, details(original.id, "  Edited  ", "  Useful note  "))
            assertEquals(original.copy(name = "Edited", note = "  Useful note  ", updatedAtEpochMillis = 6000L), repository.pin)
            assertEquals(1, repository.calls)
            assertEquals(PinPhotoChange.Keep, repository.submitted!!.photoChange)
        }

    @Test fun `compatibility editor handles missing pin without calling update`() =
        runBlocking {
            repository.pin = null
            assertEquals(PinUpdateResult.PIN_NOT_FOUND, UpdatePinDetailsUseCase(repository)(original.id, "Name", "Note"))
            assertEquals(0, repository.calls)
        }

    private class FakeRepository(var pin: Pin?) : PinRepository {
        var result = PinUpdateResult.SUCCESS
        var submitted: PinUpdate? = null
        var calls = 0
        var cancel = false

        override fun observePins(): Flow<List<Pin>> = MutableStateFlow(listOfNotNull(pin))

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(pin?.takeIf { it.id == pinId })

        override suspend fun savePin(pin: Pin) = error("Update cannot insert")

        override suspend fun deletePin(pinId: PinId) = error("Update cannot delete")

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult {
            calls++
            submitted = update
            if (cancel) throw CancellationException("Cancelled")
            val current = pin ?: return PinUpdateResult.PIN_NOT_FOUND
            if (result == PinUpdateResult.SUCCESS || result == PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING) {
                pin =
                    current.copy(
                        categoryId = update.categoryId, name = update.name, note = update.note,
                        isFavorite = update.isFavorite, updatedAtEpochMillis = updatedAtEpochMillis,
                        photoReference =
                            when (val change = update.photoChange) {
                                PinPhotoChange.Keep -> current.photoReference
                                PinPhotoChange.Remove -> null
                                is PinPhotoChange.Replace -> change.photo
                            },
                    )
            }
            return result
        }
    }
}
