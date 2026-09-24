package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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

        override fun observePins(): Flow<List<Pin>> = emptyFlow()

        override fun observePin(pinId: PinId): Flow<Pin?> = emptyFlow()

        override suspend fun savePin(pin: Pin) {
            saveError?.let { throw it }
            savedPin = pin
        }
    }
}
