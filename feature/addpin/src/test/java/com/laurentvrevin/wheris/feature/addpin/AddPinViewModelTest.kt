package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddPinViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is PermissionRequired`() {
        val fakeRepo = FakeLocationRepository(LocationResult.Timeout)
        val viewModel = AddPinViewModel(fakeRepo)

        assertEquals(AddPinUiState.PermissionRequired, viewModel.uiState.value)
    }

    @Test
    fun `onPermissionGranted transitions to Searching then Success`() =
        runTest(testDispatcher) {
            val expectedLocation =
                UserLocation(
                    position = GeoPoint(48.8566, 2.3522),
                    accuracyMeters = 5.0f,
                    timestampEpochMillis = 1000L,
                )
            val fakeRepo = FakeLocationRepository(LocationResult.Success(expectedLocation))
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)

            testScheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is AddPinUiState.Success)
            val successState = state as AddPinUiState.Success
            assertEquals(expectedLocation, successState.location)
            assertEquals(false, successState.isApproximate)
        }

    @Test
    fun `onPermissionGranted with coarse only sets isApproximate to true`() =
        runTest(testDispatcher) {
            val expectedLocation =
                UserLocation(
                    position = GeoPoint(48.8566, 2.3522),
                    timestampEpochMillis = 1000L,
                )
            val fakeRepo = FakeLocationRepository(LocationResult.Success(expectedLocation))
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = false, isCoarseGranted = true)

            testScheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is AddPinUiState.Success)
            val successState = state as AddPinUiState.Success
            assertEquals(true, successState.isApproximate)
        }

    @Test
    fun `onPermissionGranted handles ServicesDisabled`() =
        runTest(testDispatcher) {
            val fakeRepo = FakeLocationRepository(LocationResult.ServicesDisabled)
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)

            testScheduler.advanceUntilIdle()

            assertEquals(AddPinUiState.ServicesDisabled, viewModel.uiState.value)
        }

    @Test
    fun `onPermissionGranted handles Timeout`() =
        runTest(testDispatcher) {
            val fakeRepo = FakeLocationRepository(LocationResult.Timeout)
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)

            testScheduler.advanceUntilIdle()

            assertEquals(AddPinUiState.Timeout, viewModel.uiState.value)
        }

    @Test
    fun `onPermissionGranted handles TechnicalError`() =
        runTest(testDispatcher) {
            val fakeRepo = FakeLocationRepository(LocationResult.TechnicalError)
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)

            testScheduler.advanceUntilIdle()

            assertEquals(AddPinUiState.TechnicalError, viewModel.uiState.value)
        }

    @Test
    fun `onPermissionDenied with requestable sets PermissionDenied false`() {
        val fakeRepo = FakeLocationRepository(LocationResult.Timeout)
        val viewModel = AddPinViewModel(fakeRepo)

        viewModel.onPermissionDenied(isPermanentlyDenied = false)

        assertEquals(AddPinUiState.PermissionDenied(isPermanentlyDenied = false), viewModel.uiState.value)
    }

    @Test
    fun `onPermissionDenied with settings required sets PermissionDenied true`() {
        val fakeRepo = FakeLocationRepository(LocationResult.Timeout)
        val viewModel = AddPinViewModel(fakeRepo)

        viewModel.onPermissionDenied(isPermanentlyDenied = true)

        assertEquals(AddPinUiState.PermissionDenied(isPermanentlyDenied = true), viewModel.uiState.value)
    }

    @Test
    fun `retryLocation re-fetches location when permission was previously granted`() =
        runTest(testDispatcher) {
            val expectedLocation =
                UserLocation(
                    position = GeoPoint(48.8566, 2.3522),
                    timestampEpochMillis = 1000L,
                )
            val fakeRepo = FakeLocationRepository(LocationResult.ServicesDisabled)
            val viewModel = AddPinViewModel(fakeRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)
            testScheduler.advanceUntilIdle()
            assertEquals(AddPinUiState.ServicesDisabled, viewModel.uiState.value)

            fakeRepo.nextResult = LocationResult.Success(expectedLocation)
            viewModel.retryLocation()
            testScheduler.advanceUntilIdle()

            assertTrue(viewModel.uiState.value is AddPinUiState.Success)
        }

    @Test
    fun `retryLocation cancels previous in-progress acquisition`() =
        runTest(testDispatcher) {
            val slowRepo = SlowLocationRepository()
            val viewModel = AddPinViewModel(slowRepo)

            viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)
            testScheduler.runCurrent()
            assertEquals(AddPinUiState.Searching, viewModel.uiState.value)

            viewModel.retryLocation()
            testScheduler.advanceUntilIdle()

            assertEquals(1, slowRepo.completedCalls)
        }

    private class FakeLocationRepository(
        var nextResult: LocationResult,
    ) : UserLocationRepository {
        override suspend fun getCurrentLocation(): LocationResult {
            return nextResult
        }
    }

    private class SlowLocationRepository : UserLocationRepository {
        var completedCalls = 0

        override suspend fun getCurrentLocation(): LocationResult {
            delay(5000)
            completedCalls++
            return LocationResult.Timeout
        }
    }
}
