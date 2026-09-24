package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinRepository
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
    fun `repository pins are exposed to home state`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val viewModel = HomeViewModel(repository)
            val pin = testPin()

            repository.pins.value = listOf(pin)
            testScheduler.advanceUntilIdle()

            assertEquals(listOf(pin), viewModel.uiState.value.pins)
        }

    @Test
    fun `empty repository does not invent pins`() =
        runTest(dispatcher) {
            val repository = FakePinRepository()
            val viewModel = HomeViewModel(repository)

            testScheduler.advanceUntilIdle()

            assertEquals(emptyList<Pin>(), viewModel.uiState.value.pins)
        }

    private fun testPin() =
        Pin(
            id = PinId("pin-1"),
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
}
