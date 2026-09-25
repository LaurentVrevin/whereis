package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `repository pins are exposed and initial selection is null`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val locationRepository = FakeUserLocationRepository()
            val viewModel = HomeViewModel(repository, locationRepository)
            val pin = testPin("pin-1")

            repository.pins.value = listOf(pin)
            testScheduler.advanceUntilIdle()

            assertEquals(listOf(pin), viewModel.uiState.value.pins)
            assertNull(viewModel.uiState.value.selectedPinId)
        }

    @Test
    fun `pin selection updates selectedPinId and second tap keeps selection`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val locationRepository = FakeUserLocationRepository()
            val viewModel = HomeViewModel(repository, locationRepository)
            val pinA = testPin("pin-A")
            val pinB = testPin("pin-B")

            repository.pins.value = listOf(pinA, pinB)
            testScheduler.advanceUntilIdle()

            // Select A
            viewModel.onSavedPlaceSelected(pinA.id)
            assertEquals(pinA.id, viewModel.uiState.value.selectedPinId)

            // Second tap on A keeps selection
            viewModel.onSavedPlaceSelected(pinA.id)
            assertEquals(pinA.id, viewModel.uiState.value.selectedPinId)

            // Select B
            viewModel.onSavedPlaceSelected(pinB.id)
            assertEquals(pinB.id, viewModel.uiState.value.selectedPinId)
        }

    @Test
    fun `selectedPinId resets to null if selected pin disappears from repository`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val locationRepository = FakeUserLocationRepository()
            val viewModel = HomeViewModel(repository, locationRepository)
            val pinA = testPin("pin-A")
            val pinB = testPin("pin-B")

            repository.pins.value = listOf(pinA, pinB)
            testScheduler.advanceUntilIdle()

            viewModel.onSavedPlaceSelected(pinA.id)
            assertEquals(pinA.id, viewModel.uiState.value.selectedPinId)

            // Remove pinA from repository
            repository.pins.value = listOf(pinB)
            testScheduler.advanceUntilIdle()

            assertNull(viewModel.uiState.value.selectedPinId)
        }

    @Test
    fun `selecting pin fetches location and computes distance`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val locationRepository = FakeUserLocationRepository()
            val viewModel = HomeViewModel(repository, locationRepository)
            val pin =
                Pin(
                    id = PinId("pin-1"),
                    position = GeoPoint(48.8566, 2.3522),
                    categoryId = CategoryId("other"),
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 1L,
                    updatedAtEpochMillis = 1L,
                )

            repository.pins.value = listOf(pin)
            locationRepository.locationResult =
                LocationResult.Success(
                    UserLocation(
                        position = GeoPoint(48.8584, 2.2945),
                        accuracyMeters = 5f,
                        altitudeMeters = 35.0,
                        timestampEpochMillis = 1L,
                    ),
                )

            testScheduler.advanceUntilIdle()

            viewModel.onSavedPlaceSelected(pin.id)
            testScheduler.advanceUntilIdle()

            assertEquals(pin.id, viewModel.uiState.value.selectedPinId)
            assertNotNull(viewModel.uiState.value.distanceMeters)
        }

    private fun testPin(id: String) =
        Pin(
            id = PinId(id),
            position = GeoPoint(48.0, 2.0),
            categoryId = CategoryId("other"),
            accuracyMeters = null,
            altitudeMeters = null,
            createdAtEpochMillis = 1L,
            updatedAtEpochMillis = 1L,
        )

    private class FakePinRepository : PinRepository {
        val pins = MutableStateFlow<List<Pin>>(emptyList())

        override fun observePins(): Flow<List<Pin>> = pins

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(null)

        override suspend fun savePin(pin: Pin) = Unit
    }

    private class FakeUserLocationRepository : UserLocationRepository {
        var locationResult: LocationResult = LocationResult.ServicesDisabled

        override suspend fun getCurrentLocation(): LocationResult = locationResult
    }
}
