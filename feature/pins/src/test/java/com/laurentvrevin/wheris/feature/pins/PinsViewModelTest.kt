package com.laurentvrevin.wheris.feature.pins

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
class PinsViewModelTest {
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
    fun `empty repository produces Empty state`() =
        runTest {
            val fakeRepo = FakePinRepository(emptyList())
            val viewModel = PinsViewModel(fakeRepo)

            testDispatcher.scheduler.advanceUntilIdle()

            val state =
                viewModel.uiState.first {
                    it !is PinsUiState.Loading
                }

            assertTrue(state is PinsUiState.Empty)
        }

    @Test
    fun `populated repository produces Content state with stable pin ids`() =
        runTest {
            val pin1 =
                testPin(
                    id = PinId("pin-1"),
                    position = GeoPoint(10.0, 20.0),
                )

            val pin2 =
                testPin(
                    id = PinId("pin-2"),
                    position = GeoPoint(-10.0, -20.0),
                )

            val fakeRepo =
                FakePinRepository(
                    listOf(
                        pin1,
                        pin2,
                    ),
                )

            val viewModel = PinsViewModel(fakeRepo)

            testDispatcher.scheduler.advanceUntilIdle()

            val state =
                viewModel.uiState.first {
                    it is PinsUiState.Content
                } as PinsUiState.Content

            assertEquals(2, state.pins.size)
            assertEquals(PinId("pin-1"), state.pins[0].id)
            assertEquals(PinId("pin-2"), state.pins[1].id)
            assertEquals(pin1, state.pins[0].pin)
            assertEquals(pin2, state.pins[1].pin)
        }

    private fun testPin(
        id: PinId,
        position: GeoPoint,
    ): Pin =
        Pin(
            id = id,
            position = position,
            categoryId = SystemCategoryIds.PARKING,
            accuracyMeters = 5f,
            altitudeMeters = 30.0,
            createdAtEpochMillis = 1_000L,
            updatedAtEpochMillis = 1_000L,
        )

    private class FakePinRepository(
        initialPins: List<Pin> = emptyList(),
    ) : PinRepository {
        private val pins =
            MutableStateFlow(initialPins)

        override fun observePins(): Flow<List<Pin>> = pins

        override fun observePin(pinId: PinId): Flow<Pin?> =
            MutableStateFlow(
                pins.value.find {
                    it.id == pinId
                },
            )

        override suspend fun savePin(pin: Pin) {
            pins.value = pins.value + pin
        }
    }
}
