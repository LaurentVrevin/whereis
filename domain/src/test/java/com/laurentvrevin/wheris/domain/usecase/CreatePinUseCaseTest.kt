package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class CreatePinUseCaseTest {
    @Test
    fun `creates and persists pin with deterministic id timestamp and location metadata`() =
        runBlocking {
            val repository = FakePinRepository()
            val useCase =
                CreatePinUseCase(
                    pinRepository = repository,
                    idFactory = { PinId("pin-test") },
                    clock = { 42_000L },
                )
            val location =
                UserLocation(
                    position = GeoPoint(48.8566, 2.3522),
                    accuracyMeters = 7.5f,
                    altitudeMeters = 35.0,
                    timestampEpochMillis = 12_345L,
                )

            val pin = useCase(location, CategoryId("parking"))

            assertEquals(PinId("pin-test"), pin.id)
            assertEquals(location.position, pin.position)
            assertEquals(CategoryId("parking"), pin.categoryId)
            assertEquals(7.5f, pin.accuracyMeters)
            assertEquals(35.0, pin.altitudeMeters)
            assertEquals(42_000L, pin.createdAtEpochMillis)
            assertEquals(42_000L, pin.updatedAtEpochMillis)
            assertEquals(pin, repository.savedPin)
            assertNull(pin.name)
            assertNull(pin.note)
            assertFalse(pin.isFavorite)
            assertNull(pin.photoReference)
            assertEquals(1, repository.saveCount)
        }

    @Test
    fun `enriched creation normalizes name preserves formatted note and inserts once`() =
        runBlocking {
            val repository = FakePinRepository()
            val useCase = CreatePinUseCase(repository, idFactory = { PinId("enriched") }, clock = { 42_000L })
            val location = UserLocation(GeoPoint(10.0, 20.0), 7f, 35.0, 1000L)
            val photo = PhotoReference("photo-42")
            val note = "  第一行\n\n  Près du chemin 🌲  \n"

            val pin = useCase(location, CategoryId("other"), "\u2003 Café 東京 🌲 \u2003", note, true, photo)

            assertEquals("Café 東京 🌲", pin.name)
            assertEquals(note, pin.note)
            assertEquals(true, pin.isFavorite)
            assertEquals(photo, pin.photoReference)
            assertEquals(location.position, pin.position)
            assertEquals(location.accuracyMeters, pin.accuracyMeters)
            assertEquals(location.altitudeMeters, pin.altitudeMeters)
            assertEquals(42_000L, pin.createdAtEpochMillis)
            assertEquals(42_000L, pin.updatedAtEpochMillis)
            assertEquals(pin, repository.savedPin)
            assertEquals(1, repository.saveCount)
        }

    @Test
    fun `blank optional text becomes absent without requiring other details`() =
        runBlocking {
            val repository = FakePinRepository()
            val useCase = CreatePinUseCase(repository)
            val pin = useCase(UserLocation(GeoPoint(10.0, 20.0), timestampEpochMillis = 1000L), CategoryId("other"), "\u2003 ", "\n \t")
            assertNull(pin.name)
            assertNull(pin.note)
            assertFalse(pin.isFavorite)
            assertNull(pin.photoReference)
            assertEquals(1, repository.saveCount)
        }

    @Test
    fun `propagates repository failure`() {
        val repository = FakePinRepository(saveError = IOException("storage failed"))
        val useCase =
            CreatePinUseCase(
                pinRepository = repository,
                idFactory = { PinId("pin-test") },
                clock = { 42_000L },
            )
        val location =
            UserLocation(
                position = GeoPoint(1.0, 2.0),
                timestampEpochMillis = 1_000L,
            )

        assertThrows(IOException::class.java) {
            runBlocking {
                useCase(location, CategoryId("other"))
            }
        }
    }

    private class FakePinRepository(
        private val saveError: Exception? = null,
    ) : PinRepository {
        var savedPin: Pin? = null
        var saveCount = 0

        override fun observePins(): Flow<List<Pin>> = emptyFlow()

        override fun observePin(pinId: PinId): Flow<Pin?> = emptyFlow()

        override suspend fun savePin(pin: Pin) {
            saveCount++
            saveError?.let { throw it }
            savedPin = pin
        }

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult = error("Editing is not used by this test")

        override suspend fun deletePin(pinId: PinId) = Unit
    }
}
