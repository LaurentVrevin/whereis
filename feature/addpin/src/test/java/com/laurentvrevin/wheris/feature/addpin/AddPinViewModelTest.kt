package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

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
        val fixture = Fixture()

        assertEquals(AddPinUiState.PermissionRequired, fixture.viewModel.uiState.value)
    }

    @Test
    fun `fine permission transitions to position found`() =
        runTest(testDispatcher) {
            val location = testLocation()
            val fixture = Fixture(locationResult = LocationResult.Success(location))

            fixture.viewModel.onPermissionGranted(isFineGranted = true, isCoarseGranted = true)
            testScheduler.advanceUntilIdle()

            val state = fixture.viewModel.uiState.value as AddPinUiState.PositionFound
            assertEquals(location, state.location)
            assertFalse(state.isApproximate)
        }

    @Test
    fun `coarse only permission marks position approximate`() =
        runTest(testDispatcher) {
            val fixture = Fixture(locationResult = LocationResult.Success(testLocation()))

            fixture.viewModel.onPermissionGranted(isFineGranted = false, isCoarseGranted = true)
            testScheduler.advanceUntilIdle()

            val state = fixture.viewModel.uiState.value as AddPinUiState.PositionFound
            assertTrue(state.isApproximate)
        }

    @Test
    fun `location failures remain recoverable states`() =
        runTest(testDispatcher) {
            val fixture = Fixture(locationResult = LocationResult.ServicesDisabled)
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            assertEquals(AddPinUiState.ServicesDisabled, fixture.viewModel.uiState.value)

            fixture.locationRepository.nextResult = LocationResult.Timeout
            fixture.viewModel.retryLocation()
            testScheduler.advanceUntilIdle()
            assertEquals(AddPinUiState.Timeout, fixture.viewModel.uiState.value)

            fixture.locationRepository.nextResult = LocationResult.TechnicalError
            fixture.viewModel.retryLocation()
            testScheduler.advanceUntilIdle()
            assertEquals(AddPinUiState.TechnicalError, fixture.viewModel.uiState.value)
        }

    @Test
    fun `permission denied distinguishes requestable and settings required`() {
        val fixture = Fixture()

        fixture.viewModel.onPermissionDenied(isPermanentlyDenied = false)
        assertEquals(
            AddPinUiState.PermissionDenied(isPermanentlyDenied = false),
            fixture.viewModel.uiState.value,
        )

        fixture.viewModel.onPermissionDenied(isPermanentlyDenied = true)
        assertEquals(
            AddPinUiState.PermissionDenied(isPermanentlyDenied = true),
            fixture.viewModel.uiState.value,
        )
    }

    @Test
    fun `confirm position loads system categories and preserves location`() =
        runTest(testDispatcher) {
            val location = testLocation()
            val fixture = Fixture(locationResult = LocationResult.Success(location))
            fixture.categoryRepository.categories.value = systemCategories()

            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            fixture.viewModel.confirmPosition()
            testScheduler.advanceUntilIdle()

            val state = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertEquals(location, state.location)
            assertEquals(SystemCategoryIds.ALL, state.categories.map { it.id })
            assertFalse(state.isLoadingCategories)
        }

    @Test
    fun `select category then save persists once and reaches Saved`() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()

            fixture.viewModel.selectCategory(SystemCategoryIds.PARKING)
            fixture.viewModel.savePin()
            fixture.viewModel.savePin()
            testScheduler.advanceUntilIdle()

            assertEquals(1, fixture.pinRepository.saveCalls)
            val saved = fixture.viewModel.uiState.value as AddPinUiState.Saved
            assertEquals(SystemCategoryIds.PARKING, saved.pin.categoryId)
            assertEquals(testLocation().position, saved.pin.position)
        }

    @Test
    fun `save failure preserves draft and selected category then retry succeeds`() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.selectCategory(SystemCategoryIds.RESTAURANT)
            fixture.pinRepository.shouldFail = true

            fixture.viewModel.savePin()
            testScheduler.advanceUntilIdle()

            val failed = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertEquals(testLocation(), failed.location)
            assertEquals(SystemCategoryIds.RESTAURANT, failed.selectedCategoryId)
            assertTrue(failed.saveFailed)

            fixture.pinRepository.shouldFail = false
            fixture.viewModel.savePin()
            testScheduler.advanceUntilIdle()

            assertTrue(fixture.viewModel.uiState.value is AddPinUiState.Saved)
            assertEquals(2, fixture.pinRepository.saveCalls)
        }

    @Test
    fun `back from category restores accepted position`() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()

            fixture.viewModel.backToPosition()

            val state = fixture.viewModel.uiState.value as AddPinUiState.PositionFound
            assertEquals(testLocation(), state.location)
        }

    @Test
    fun `retry location cancels previous in progress acquisition`() =
        runTest(testDispatcher) {
            val slowRepository = SlowLocationRepository()
            val fixture = Fixture(locationRepositoryOverride = slowRepository)

            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.runCurrent()
            fixture.viewModel.retryLocation()
            testScheduler.advanceUntilIdle()

            assertEquals(1, slowRepository.completedCalls)
        }

    private suspend fun readyToSaveFixture(): Fixture {
        val fixture = Fixture(locationResult = LocationResult.Success(testLocation()))
        fixture.categoryRepository.categories.value = systemCategories()
        fixture.viewModel.onPermissionGranted(true, true)
        testDispatcher.scheduler.advanceUntilIdle()
        fixture.viewModel.confirmPosition()
        testDispatcher.scheduler.advanceUntilIdle()
        return fixture
    }

    private fun testLocation() =
        UserLocation(
            position = GeoPoint(48.8566, 2.3522),
            accuracyMeters = 5.0f,
            altitudeMeters = 35.0,
            timestampEpochMillis = 1_000L,
        )

    private fun systemCategories(): List<Category> = SystemCategoryIds.ALL.map { Category(id = it, isSystem = true) }

    private inner class Fixture(
        locationResult: LocationResult = LocationResult.Timeout,
        locationRepositoryOverride: UserLocationRepository? = null,
    ) {
        val locationRepository = FakeLocationRepository(locationResult)
        val categoryRepository = FakeCategoryRepository()
        val pinRepository = FakePinRepository()
        private val createPinUseCase =
            CreatePinUseCase(
                pinRepository = pinRepository,
                idFactory = { PinId("created-pin") },
                clock = { 10_000L },
            )
        val viewModel =
            AddPinViewModel(
                userLocationRepository = locationRepositoryOverride ?: locationRepository,
                categoryRepository = categoryRepository,
                createPinUseCase = createPinUseCase,
            )
    }

    private class FakeLocationRepository(
        var nextResult: LocationResult,
    ) : UserLocationRepository {
        override suspend fun getCurrentLocation(): LocationResult = nextResult
    }

    private class SlowLocationRepository : UserLocationRepository {
        var completedCalls = 0

        override suspend fun getCurrentLocation(): LocationResult {
            delay(5_000)
            completedCalls++
            return LocationResult.Timeout
        }
    }

    private class FakeCategoryRepository : CategoryRepository {
        val categories = MutableStateFlow<List<Category>>(emptyList())

        override fun observeSystemCategories(): Flow<List<Category>> = categories
    }

    private class FakePinRepository : PinRepository {
        var saveCalls = 0
        var shouldFail = false
        val pins = MutableStateFlow<List<Pin>>(emptyList())

        override fun observePins(): Flow<List<Pin>> = pins

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(pins.value.find { it.id == pinId })

        override suspend fun savePin(pin: Pin) {
            saveCalls++
            if (shouldFail) throw IOException("save failed")
            pins.value = pins.value + pin
        }
    }
}
