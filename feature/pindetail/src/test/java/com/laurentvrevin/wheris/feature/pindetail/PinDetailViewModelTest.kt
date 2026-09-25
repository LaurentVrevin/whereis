package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
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

    private suspend fun assertLocationFailurePreservesPin(result: LocationResult) {
        val pin = testPin()

        val viewModel =
            PinDetailViewModel(
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
        private val pin = MutableStateFlow(initialPin)

        override fun observePins(): Flow<List<Pin>> =
            MutableStateFlow(
                pin.value?.let(::listOf).orEmpty(),
            )

        override fun observePin(pinId: PinId): Flow<Pin?> = pin

        override suspend fun savePin(pin: Pin) = Unit
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
    }

    private class FakeLocationRepository(
        private val result: LocationResult,
    ) : UserLocationRepository {
        override suspend fun getCurrentLocation(): LocationResult = result
    }
}
