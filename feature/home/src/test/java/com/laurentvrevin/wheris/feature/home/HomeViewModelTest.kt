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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun `onMapSelectionCleared resets selectedPinId to null`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val locationRepository = FakeUserLocationRepository()
            val viewModel = HomeViewModel(repository, locationRepository)
            val pin = testPin("pin-1")

            repository.pins.value = listOf(pin)
            testScheduler.advanceUntilIdle()

            viewModel.onSavedPlaceSelected(pin.id)
            assertEquals(pin.id, viewModel.uiState.value.selectedPinId)

            viewModel.onMapSelectionCleared()
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

    @Test
    fun `map readiness and initial camera focus logic`() {
        val pin = testPin("pin-1")
        val userLocation =
            UserLocation(
                position = GeoPoint(48.8584, 2.2945),
                accuracyMeters = 5f,
                altitudeMeters = 35.0,
                timestampEpochMillis = 1L,
            )

        // Case A: Location acquiring, saved places available -> not ready, no focus yet (prevents saved place race)
        val stateAcquiring = HomeUiState(pins = listOf(pin), userLocation = null, locationResolved = false)
        assertFalse(stateAcquiring.isReady)
        assertNull(stateAcquiring.initialCameraFocus)

        // Case B: User Location available -> ready, focus is User Location
        val stateWithUserLoc = HomeUiState(pins = listOf(pin), userLocation = userLocation, locationResolved = true)
        assertTrue(stateWithUserLoc.isReady)
        assertEquals(userLocation.position, stateWithUserLoc.initialCameraFocus)

        // Case C: Location unavailable (resolved = true, userLocation = null), saved places available -> ready, focus is saved place fallback
        val stateFallback = HomeUiState(pins = listOf(pin), userLocation = null, locationResolved = true)
        assertTrue(stateFallback.isReady)
        assertEquals(pin.position, stateFallback.initialCameraFocus)

        // Case D: Location unavailable, no saved places -> ready, focus is null
        val stateEmpty = HomeUiState(pins = emptyList(), userLocation = null, locationResolved = true)
        assertTrue(stateEmpty.isReady)
        assertNull(stateEmpty.initialCameraFocus)
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

        override suspend fun updatePinDetails(
            pinId: PinId,
            name: String?,
            note: String?,
            updatedAtEpochMillis: Long,
        ): Boolean = error("Editing is not used by this test")

        override suspend fun deletePin(pinId: PinId) {
            pins.value = pins.value.filterNot { it.id == pinId }
        }
    }

    private class FakeUserLocationRepository : UserLocationRepository {
        var locationResult: LocationResult = LocationResult.ServicesDisabled

        override suspend fun getCurrentLocation(): LocationResult = locationResult
    }
}
