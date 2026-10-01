package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
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
import kotlinx.coroutines.CompletableDeferred
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

    @Test
    fun `explicit retry replaces found position and saves the new location`() =
        runTest(testDispatcher) {
            val fixture = Fixture(LocationResult.Success(testLocation()))
            fixture.categoryRepository.categories.value = systemCategories()
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            val newLocation = testLocation().copy(position = GeoPoint(12.0, 24.0), timestampEpochMillis = 2_000L)
            fixture.locationRepository.nextResult = LocationResult.Success(newLocation)

            fixture.viewModel.retryLocation(isFineGranted = false, isCoarseGranted = true)
            assertEquals(AddPinUiState.Searching, fixture.viewModel.uiState.value)
            testScheduler.advanceUntilIdle()
            val found = fixture.viewModel.uiState.value as AddPinUiState.PositionFound
            assertEquals(newLocation, found.location)
            assertTrue(found.isApproximate)
            fixture.viewModel.confirmPosition()
            testScheduler.advanceUntilIdle()
            fixture.viewModel.selectCategory(SystemCategoryIds.PARKING)
            fixture.viewModel.savePin()
            testScheduler.advanceUntilIdle()

            val saved = fixture.viewModel.uiState.value as AddPinUiState.Saved
            assertEquals(newLocation.position, saved.pin.position)
            assertEquals(newLocation.accuracyMeters, saved.pin.accuracyMeters)
            assertEquals(newLocation.altitudeMeters, saved.pin.altitudeMeters)
            assertEquals(2, fixture.locationRepository.calls)
        }

    @Test
    fun `permission notifications preserve position category and location failure`() =
        runTest(testDispatcher) {
            val fixture = Fixture(LocationResult.Success(testLocation()))
            fixture.categoryRepository.categories.value = systemCategories()
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            val found = fixture.viewModel.uiState.value
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            assertEquals(found, fixture.viewModel.uiState.value)

            fixture.viewModel.confirmPosition()
            testScheduler.advanceUntilIdle()
            fixture.viewModel.selectCategory(SystemCategoryIds.PARKING)
            val selected = fixture.viewModel.uiState.value
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            assertEquals(selected, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.locationRepository.calls)

            fixture.viewModel.backToPosition()
            fixture.locationRepository.nextResult = LocationResult.Timeout
            fixture.viewModel.retryLocation()
            testScheduler.advanceUntilIdle()
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            assertEquals(AddPinUiState.Timeout, fixture.viewModel.uiState.value)
            assertEquals(2, fixture.locationRepository.calls)
        }

    @Test
    fun `retry without permission requests permission and denial remains recoverable`() =
        runTest(testDispatcher) {
            val fixture = Fixture(LocationResult.Success(testLocation()))
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.advanceUntilIdle()
            fixture.viewModel.onPermissionDenied(true)
            fixture.viewModel.retryLocation()
            assertEquals(AddPinUiState.PermissionRequired, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.locationRepository.calls)
            fixture.viewModel.retryLocation(isFineGranted = false, isCoarseGranted = false)
            assertEquals(AddPinUiState.PermissionRequired, fixture.viewModel.uiState.value)
            fixture.viewModel.onPermissionDenied(true)
            fixture.viewModel.retryLocation()
            assertEquals(AddPinUiState.PermissionRequired, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.locationRepository.calls)
            fixture.viewModel.onPermissionGranted(false, true)
            testScheduler.advanceUntilIdle()
            assertTrue((fixture.viewModel.uiState.value as AddPinUiState.PositionFound).isApproximate)
        }

    @Test
    fun `back and double click during suspended save preserve draft until persistence succeeds`() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.selectCategory(SystemCategoryIds.PARKING)
            val completion = CompletableDeferred<Unit>()
            fixture.pinRepository.saveCompletion = completion
            fixture.viewModel.savePin()
            testScheduler.runCurrent()
            val saving = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertTrue(saving.isSaving)
            assertTrue(fixture.pinRepository.pins.value.isEmpty())

            fixture.viewModel.backToPosition()
            fixture.viewModel.savePin()
            fixture.viewModel.selectCategory(SystemCategoryIds.RESTAURANT)
            fixture.viewModel.onPermissionGranted(true, true)
            fixture.viewModel.retryLocation()
            testScheduler.runCurrent()
            assertEquals(saving, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.pinRepository.saveCalls)

            completion.complete(Unit)
            testScheduler.advanceUntilIdle()
            val saved = fixture.viewModel.uiState.value as AddPinUiState.Saved
            assertEquals(listOf(saved.pin), fixture.pinRepository.pins.value)
            assertEquals(SystemCategoryIds.PARKING, saved.pin.categoryId)
            assertEquals(testLocation().position, saved.pin.position)
            fixture.viewModel.savePin()
            fixture.viewModel.backToPosition()
            assertEquals(saved, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.pinRepository.saveCalls)
        }

    @Test
    fun `suspended save failure retains draft and retry creates only one place`() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.selectCategory(SystemCategoryIds.RESTAURANT)
            val draft = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            val completion = CompletableDeferred<Unit>()
            fixture.pinRepository.saveCompletion = completion
            fixture.viewModel.savePin()
            testScheduler.runCurrent()
            fixture.viewModel.backToPosition()
            fixture.viewModel.savePin()
            completion.completeExceptionally(IOException("save failed"))
            testScheduler.advanceUntilIdle()
            assertEquals(draft.copy(saveFailed = true), fixture.viewModel.uiState.value)
            assertTrue(fixture.pinRepository.pins.value.isEmpty())
            assertEquals(1, fixture.pinRepository.saveCalls)

            val retryCompletion = CompletableDeferred<Unit>()
            fixture.pinRepository.saveCompletion = retryCompletion
            fixture.viewModel.savePin()
            fixture.viewModel.savePin()
            testScheduler.runCurrent()
            assertEquals(2, fixture.pinRepository.saveCalls)
            assertTrue((fixture.viewModel.uiState.value as AddPinUiState.CategorySelection).isSaving)
            retryCompletion.complete(Unit)
            testScheduler.advanceUntilIdle()
            assertTrue(fixture.viewModel.uiState.value is AddPinUiState.Saved)
            assertEquals(1, fixture.pinRepository.pins.value.size)
            assertEquals(SystemCategoryIds.RESTAURANT, fixture.pinRepository.pins.value.single().categoryId)
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

    @Test
    fun customCategoryIsVisibleAndCreationCancellationRestoresTheWholeDraft() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            val custom = Category(CategoryId("existing"), false, "散歩", CategoryIconKey.PARK, CategoryColorKey.GREEN, 1L)
            fixture.categoryRepository.categories.value = systemCategories() + custom
            testScheduler.runCurrent()
            fixture.viewModel.selectCategory(custom.id)
            val previous = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            fixture.viewModel.openCategoryCreation()
            assertEquals(previous, (fixture.viewModel.uiState.value as AddPinUiState.CategoryCreation).selection)
            fixture.viewModel.retryLocation()
            fixture.viewModel.updateCategoryName("Draft")
            fixture.viewModel.cancelCategoryCreation()
            assertEquals(previous, fixture.viewModel.uiState.value)
            assertEquals(1, fixture.locationRepository.calls)
            fixture.viewModel.backToPosition()
            assertEquals(previous.location, (fixture.viewModel.uiState.value as AddPinUiState.PositionFound).location)
        }

    @Test
    fun whitespaceCannotCreateAndNameIconAndColorArePersistedAfterNormalization() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.openCategoryCreation()
            fixture.viewModel.updateCategoryName(" \t\n ")
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            assertEquals(0, fixture.categoryRepository.createCalls)
            fixture.viewModel.updateCategoryName("  茸 — Champignons  ")
            fixture.viewModel.selectCategoryIcon(CategoryIconKey.PARK)
            fixture.viewModel.selectCategoryColor(CategoryColorKey.PURPLE)
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            val category = requireNotNull(fixture.categoryRepository.created)
            assertEquals("茸 — Champignons", category.name)
            assertEquals(CategoryIconKey.PARK, category.iconKey)
            assertEquals(CategoryColorKey.PURPLE, category.colorKey)
            assertEquals(1, fixture.categoryRepository.createCalls)
        }

    @Test
    fun creationBlocksDuplicateSubmissionEditingAndBackUntilPersistenceSucceeds() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            val original = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            val completion = CompletableDeferred<Unit>()
            fixture.categoryRepository.createCompletion = completion
            fixture.viewModel.openCategoryCreation()
            fixture.viewModel.updateCategoryName("Camping")
            fixture.viewModel.createCategory()
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            fixture.viewModel.cancelCategoryCreation()
            fixture.viewModel.updateCategoryName("Changed")
            val creating = fixture.viewModel.uiState.value as AddPinUiState.CategoryCreation
            assertTrue(creating.isCreating)
            assertEquals("Camping", creating.name)
            assertEquals(original.location, creating.selection.location)
            assertEquals(1, fixture.categoryRepository.createCalls)
            completion.complete(Unit)
            testScheduler.runCurrent()
            assertTrue(fixture.viewModel.uiState.value is AddPinUiState.CategorySelection)
        }

    @Test
    fun creationErrorPreservesFormAndRetrySucceeds() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.openCategoryCreation()
            fixture.viewModel.updateCategoryName("Camping")
            fixture.viewModel.selectCategoryIcon(CategoryIconKey.PARK)
            fixture.viewModel.selectCategoryColor(CategoryColorKey.TEAL)
            fixture.categoryRepository.shouldFail = true
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            val failed = fixture.viewModel.uiState.value as AddPinUiState.CategoryCreation
            assertTrue(failed.creationFailed)
            assertFalse(failed.isCreating)
            assertEquals("Camping", failed.name)
            assertEquals(CategoryIconKey.PARK, failed.iconKey)
            assertEquals(CategoryColorKey.TEAL, failed.colorKey)
            assertEquals(testLocation(), failed.selection.location)
            fixture.categoryRepository.shouldFail = false
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            assertEquals(
                requireNotNull(fixture.categoryRepository.created).id,
                (fixture.viewModel.uiState.value as AddPinUiState.CategorySelection).selectedCategoryId,
            )
        }

    @Test
    fun returnedCategoryIsImmediatelySelectedAndSurvivesDelayedFlowThenSavesThePlace() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.openCategoryCreation()
            fixture.viewModel.updateCategoryName("Camping")
            fixture.viewModel.createCategory()
            testScheduler.runCurrent()
            val category = requireNotNull(fixture.categoryRepository.created)
            val immediate = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertEquals(category.id, immediate.selectedCategoryId)
            assertTrue(category in immediate.categories)
            assertEquals(testLocation(), immediate.location)
            // Simulate a queued older Room emission before the new category is observed.
            fixture.categoryRepository.categories.value = systemCategories().dropLast(1)
            testScheduler.runCurrent()
            assertTrue(category in (fixture.viewModel.uiState.value as AddPinUiState.CategorySelection).categories)
            fixture.categoryRepository.categories.value = systemCategories() + category
            testScheduler.runCurrent()
            val canonical = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertEquals(category.id, canonical.selectedCategoryId)
            assertEquals(1, canonical.categories.count { it.id == category.id })
            fixture.viewModel.savePin()
            testScheduler.runCurrent()
            val pin = (fixture.viewModel.uiState.value as AddPinUiState.Saved).pin
            assertEquals(category.id, pin.categoryId)
            assertEquals(testLocation().position, pin.position)
            assertEquals(testLocation().accuracyMeters, pin.accuracyMeters)
            assertEquals(testLocation().altitudeMeters, pin.altitudeMeters)
            assertEquals(1, fixture.locationRepository.calls)
        }

    @Test
    fun categoryFlowContinuesWhileEditorIsOpenAndCancellationKeepsItsLatestList() =
        runTest(testDispatcher) {
            val fixture = readyToSaveFixture()
            fixture.viewModel.selectCategory(SystemCategoryIds.PARKING)
            fixture.viewModel.openCategoryCreation()
            val custom = Category(CategoryId("external"), false, "Photo", CategoryIconKey.PHOTO_CAMERA, CategoryColorKey.BLUE, 1L)
            fixture.categoryRepository.categories.value = systemCategories() + custom
            testScheduler.runCurrent()
            fixture.viewModel.cancelCategoryCreation()
            val selection = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertTrue(custom in selection.categories)
            assertEquals(SystemCategoryIds.PARKING, selection.selectedCategoryId)
            assertEquals(testLocation(), selection.location)
        }

    @Test
    fun categoryLoadingErrorIsNotAnEmptySuccessfulSelection() =
        runTest(testDispatcher) {
            val fixture = Fixture(locationResult = LocationResult.Success(testLocation()))
            fixture.categoryRepository.observeFailure = true
            fixture.viewModel.onPermissionGranted(true, true)
            testScheduler.runCurrent()
            fixture.viewModel.confirmPosition()
            testScheduler.runCurrent()
            val selection = fixture.viewModel.uiState.value as AddPinUiState.CategorySelection
            assertTrue(selection.categoryLoadFailed)
            assertFalse(selection.isLoadingCategories)
            fixture.viewModel.openCategoryCreation()
            assertEquals(selection, fixture.viewModel.uiState.value)
        }

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
        var calls = 0

        override suspend fun getCurrentLocation(): LocationResult {
            calls++
            return nextResult
        }
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

        var createCalls = 0
        var shouldFail = false
        var createCompletion: CompletableDeferred<Unit>? = null
        var created: Category? = null
        var observeFailure = false

        override fun observeCategories(): Flow<List<Category>> =
            if (observeFailure) kotlinx.coroutines.flow.flow { throw IOException("load failed") } else categories

        override suspend fun createCustomCategory(
            name: String,
            iconKey: com.laurentvrevin.wheris.core.model.CategoryIconKey,
            colorKey: com.laurentvrevin.wheris.core.model.CategoryColorKey,
        ): Category {
            createCalls++
            createCompletion?.await()
            if (shouldFail) throw IOException("creation failed")
            return Category(CategoryId("custom-$createCalls"), false, name, iconKey, colorKey, 42_000L).also { created = it }
        }

        override fun observeSystemCategories(): Flow<List<Category>> = error("Add Pin must observe system and custom")
    }

    private class FakePinRepository : PinRepository {
        var saveCalls = 0
        var shouldFail = false
        var saveCompletion: CompletableDeferred<Unit>? = null
        val pins = MutableStateFlow<List<Pin>>(emptyList())

        override fun observePins(): Flow<List<Pin>> = pins

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(pins.value.find { it.id == pinId })

        override suspend fun savePin(pin: Pin) {
            saveCalls++
            saveCompletion?.await()
            if (shouldFail) throw IOException("save failed")
            pins.value = pins.value + pin
        }

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
}
