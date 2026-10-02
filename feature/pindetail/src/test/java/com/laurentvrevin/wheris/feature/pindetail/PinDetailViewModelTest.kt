package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PinDetailViewModelTest {
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
    fun `existing pin becomes Content without current location`() =
        runTest {
            val pin = testPin()
            val viewModel =
                PinDetailViewModel(
                    categoryRepository = FakeCategoryRepository(),
                    pinRepository = FakePinRepository(pin),
                    userLocationRepository = FakeLocationRepository(LocationResult.Timeout),
                )

            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is PinDetailUiState.Content)

            state as PinDetailUiState.Content

            assertEquals(pin, state.pin)
            assertNull(state.distanceMeters)
            assertNull(state.cardinalDirection)
        }

    @Test
    fun `missing pin becomes NotFound`() =
        runTest {
            val viewModel =
                PinDetailViewModel(
                    categoryRepository = FakeCategoryRepository(),
                    pinRepository = FakePinRepository(null),
                    userLocationRepository = FakeLocationRepository(LocationResult.Timeout),
                )

            viewModel.observePin(PinId("missing"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                PinDetailUiState.NotFound,
                viewModel.uiState.value,
            )
        }

    @Test
    fun `repository failure becomes Error`() =
        runTest {
            val viewModel =
                PinDetailViewModel(
                    categoryRepository = FakeCategoryRepository(),
                    pinRepository = ThrowingPinRepository(),
                    userLocationRepository = FakeLocationRepository(LocationResult.Timeout),
                )

            viewModel.observePin(PinId("pin-error"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                PinDetailUiState.Error,
                viewModel.uiState.value,
            )
        }

    @Test
    fun `successful current location enriches content with distance and direction`() =
        runTest {
            val pin =
                testPin(
                    position = GeoPoint(0.0, 1.0),
                )

            val currentLocation =
                UserLocation(
                    position = GeoPoint(0.0, 0.0),
                    accuracyMeters = 5f,
                    altitudeMeters = null,
                    timestampEpochMillis = 1_000L,
                )

            val viewModel =
                PinDetailViewModel(
                    categoryRepository = FakeCategoryRepository(),
                    pinRepository = FakePinRepository(pin),
                    userLocationRepository =
                        FakeLocationRepository(
                            LocationResult.Success(currentLocation),
                        ),
                )

            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.requestCurrentLocation()
            testDispatcher.scheduler.advanceUntilIdle()

            val state =
                viewModel.uiState.value as PinDetailUiState.Content

            assertEquals(
                111_194.9,
                state.distanceMeters!!,
                2.0,
            )
            assertEquals(
                CardinalDirection.EAST,
                state.cardinalDirection,
            )
        }

    @Test
    fun `services disabled preserves pin and leaves derived location data absent`() =
        runTest {
            assertLocationFailurePreservesPin(
                LocationResult.ServicesDisabled,
            )
        }

    @Test
    fun `timeout preserves pin and leaves derived location data absent`() =
        runTest {
            assertLocationFailurePreservesPin(
                LocationResult.Timeout,
            )
        }

    @Test
    fun `technical error preserves pin and leaves derived location data absent`() =
        runTest {
            assertLocationFailurePreservesPin(
                LocationResult.TechnicalError,
            )
        }

    @Test
    fun `deletion requires confirmation and cancelling preserves the place`() =
        runTest {
            val pin = testPin()
            val repository = FakePinRepository(pin)
            val viewModel = PinDetailViewModel(repository, FakeLocationRepository(LocationResult.Timeout), FakeCategoryRepository())
            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.confirmDeletion()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(0, repository.deleteCalls)

            viewModel.requestDeletion()
            assertEquals(PinDeletionState.Confirmation, (viewModel.uiState.value as PinDetailUiState.Content).deletion)
            viewModel.cancelDeletion()
            viewModel.confirmDeletion()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(0, repository.deleteCalls)
            assertEquals(pin, repository.pin.value)
            assertEquals(PinDeletionState.None, (viewModel.uiState.value as PinDetailUiState.Content).deletion)
        }

    @Test
    fun `deletion waits for persistence despite null emissions and ignores double confirmation`() =
        runTest {
            val pin = testPin()
            val repository = FakePinRepository(pin)
            val completion = CompletableDeferred<Unit>()
            repository.deleteCompletion = completion
            val viewModel = PinDetailViewModel(repository, FakeLocationRepository(LocationResult.Timeout), FakeCategoryRepository())
            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.requestDeletion()
            viewModel.confirmDeletion()
            testDispatcher.scheduler.runCurrent()
            assertNull(repository.pin.value)
            assertEquals(PinDeletionState.InProgress, (viewModel.uiState.value as PinDetailUiState.Content).deletion)

            viewModel.confirmDeletion()
            viewModel.cancelDeletion()
            viewModel.observePin(pin.id)
            viewModel.requestCurrentLocation()
            testDispatcher.scheduler.runCurrent()
            assertEquals(1, repository.deleteCalls)
            assertEquals(PinDeletionState.InProgress, (viewModel.uiState.value as PinDetailUiState.Content).deletion)

            completion.complete(Unit)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PinDetailUiState.Deleted, viewModel.uiState.value)
        }

    @Test
    fun `delete failure keeps the place visible and permits retry`() =
        runTest {
            val pin = testPin()
            val repository = FakePinRepository(pin)
            repository.deleteFailure = IOException("storage unavailable")
            val viewModel = PinDetailViewModel(repository, FakeLocationRepository(LocationResult.Timeout), FakeCategoryRepository())
            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.requestDeletion()
            viewModel.confirmDeletion()
            testDispatcher.scheduler.advanceUntilIdle()
            val failed = viewModel.uiState.value as PinDetailUiState.Content
            assertEquals(PinDeletionState.Failed, failed.deletion)
            assertEquals(pin, failed.pin)
            assertEquals(pin, repository.pin.value)

            repository.deleteFailure = null
            viewModel.confirmDeletion()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, repository.deleteCalls)
            assertEquals(PinDetailUiState.Deleted, viewModel.uiState.value)
        }

    @Test
    fun `location and repository updates preserve the pending confirmation`() =
        runTest {
            val pin = testPin()
            val repository = FakePinRepository(pin)
            val viewModel = PinDetailViewModel(repository, FakeLocationRepository(LocationResult.Timeout), FakeCategoryRepository())
            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.requestDeletion()

            repository.pin.value = pin.copy(accuracyMeters = 12f)
            viewModel.requestCurrentLocation()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value as PinDetailUiState.Content
            assertEquals(PinDeletionState.Confirmation, state.deletion)
            assertEquals(12f, state.pin.accuracyMeters)
            assertEquals(0, repository.deleteCalls)
        }

    private suspend fun assertLocationFailurePreservesPin(result: LocationResult) {
        val pin = testPin()

        val viewModel =
            PinDetailViewModel(
                categoryRepository = FakeCategoryRepository(),
                pinRepository = FakePinRepository(pin),
                userLocationRepository = FakeLocationRepository(result),
            )

        viewModel.observePin(pin.id)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.requestCurrentLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        val state =
            viewModel.uiState.value as PinDetailUiState.Content

        assertEquals(pin, state.pin)
        assertNull(state.distanceMeters)
        assertNull(state.cardinalDirection)
    }

    @Test
    fun customCategoryIsResolvedAndKeptWhenLocationUpdates() =
        runTest {
            val categories = FakeCategoryRepository()
            val custom =
                com.laurentvrevin.wheris.core.model.Category(
                    com.laurentvrevin.wheris.core.model.CategoryId("custom"),
                    false,
                    "Champignons",
                    com.laurentvrevin.wheris.core.model.CategoryIconKey.PARK,
                    com.laurentvrevin.wheris.core.model.CategoryColorKey.PURPLE,
                    1000L,
                )
            categories.categories.value = listOf(custom)
            val pin = testPin().copy(categoryId = custom.id)
            val viewModel = PinDetailViewModel(FakePinRepository(pin), FakeLocationRepository(LocationResult.Timeout), categories)
            viewModel.observePin(pin.id)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(custom, (viewModel.uiState.value as PinDetailUiState.Content).category)
            val updated =
                custom.copy(
                    name = "Promenades",
                    iconKey = com.laurentvrevin.wheris.core.model.CategoryIconKey.PLACE,
                    colorKey = com.laurentvrevin.wheris.core.model.CategoryColorKey.TEAL,
                )
            categories.categories.value = listOf(updated)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(updated, (viewModel.uiState.value as PinDetailUiState.Content).category)
            assertEquals(pin, (viewModel.uiState.value as PinDetailUiState.Content).pin)
            viewModel.requestCurrentLocation()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(updated, (viewModel.uiState.value as PinDetailUiState.Content).category)
        }

    private fun testPin(position: GeoPoint = GeoPoint(10.0, 20.0)): Pin =
        Pin(
            id = PinId("pin-test"),
            position = position,
            categoryId = SystemCategoryIds.PARKING,
            accuracyMeters = 7f,
            altitudeMeters = 30.0,
            createdAtEpochMillis = 1_000L,
            updatedAtEpochMillis = 1_000L,
        )

    private class FakePinRepository(
        initialPin: Pin?,
    ) : PinRepository {
        val pin = MutableStateFlow(initialPin)
        var deleteCalls = 0
        var deleteFailure: Exception? = null
        var deleteCompletion: CompletableDeferred<Unit>? = null

        override fun observePins(): Flow<List<Pin>> =
            MutableStateFlow(
                pin.value?.let(::listOf).orEmpty(),
            )

        override fun observePin(pinId: PinId): Flow<Pin?> = pin

        override suspend fun savePin(pin: Pin) = Unit

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult = error("Editing is not used by this test")

        override suspend fun deletePin(pinId: PinId) {
            deleteCalls++
            deleteFailure?.let { throw it }
            if (pin.value?.id == pinId) pin.value = null
            deleteCompletion?.await()
        }
    }

    private class ThrowingPinRepository : PinRepository {
        override fun observePins(): Flow<List<Pin>> =
            flow {
                error("boom")
            }

        override fun observePin(pinId: PinId): Flow<Pin?> =
            flow {
                error("boom")
            }

        override suspend fun savePin(pin: Pin) = Unit

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult = error("Editing is not used by this test")

        override suspend fun deletePin(pinId: PinId) = error("boom")
    }

    private class FakeLocationRepository(
        private val result: LocationResult,
    ) : UserLocationRepository {
        override suspend fun getCurrentLocation(): LocationResult = result
    }
}
