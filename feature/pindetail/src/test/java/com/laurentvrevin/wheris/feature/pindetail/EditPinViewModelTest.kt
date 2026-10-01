package com.laurentvrevin.wheris.feature.pindetail

import androidx.lifecycle.SavedStateHandle
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.usecase.UpdatePinDetailsUseCase
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EditPinViewModelTest {
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
    fun `editing stays local until save and updates only intended metadata`() =
        runTest {
            val repository = FakeRepository()
            val original = repository.pin!!
            val viewModel = editor(repository)
            viewModel.load(original.id)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals("Original", (viewModel.uiState.value as EditPinUiState.Content).name)

            viewModel.changeName("  Camping  ")
            viewModel.changeNote("À côté du chemin\n  Garder cette indentation")
            assertEquals(original, repository.pin)
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(EditPinUiState.Saved, viewModel.uiState.value)
            assertEquals(
                original.copy(name = "Camping", note = "À côté du chemin\n  Garder cette indentation", updatedAtEpochMillis = 5000L),
                repository.pin,
            )
        }

    @Test
    fun `failed save retains draft and retry can clear optional fields`() =
        runTest {
            val repository = FakeRepository()
            val viewModel = editor(repository)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeName("   ")
            viewModel.changeNote("")
            repository.fail = true
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()

            val failed = viewModel.uiState.value as EditPinUiState.Content
            assertTrue(failed.saveFailed)
            assertEquals("   ", failed.name)
            assertEquals("", failed.note)
            assertEquals("Original", repository.pin!!.name)

            repository.fail = false
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(null, repository.pin!!.name)
            assertEquals(null, repository.pin!!.note)
            assertEquals(EditPinUiState.Saved, viewModel.uiState.value)
        }

    @Test
    fun `double save and edits while saving cannot change the submitted draft`() =
        runTest {
            val repository = FakeRepository()
            repository.completion = CompletableDeferred()
            val viewModel = editor(repository)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeName("Chosen name")
            viewModel.save()
            viewModel.save()
            viewModel.changeName("Ignored")
            dispatcher.scheduler.runCurrent()
            assertEquals(1, repository.updateCalls)
            assertTrue((viewModel.uiState.value as EditPinUiState.Content).isSaving)

            repository.completion!!.complete(Unit)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals("Chosen name", repository.pin!!.name)
        }

    @Test
    fun `restored editor recovers unsaved text without persisting it`() =
        runTest {
            val repository = FakeRepository()
            val handle = SavedStateHandle()
            val viewModel = editor(repository, handle)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeName("Draft")
            viewModel.changeNote("Unsaved note")

            val restoredHandle = SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) })
            val restored = editor(repository, restoredHandle)
            restored.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()

            val state = restored.uiState.value as EditPinUiState.Content
            assertEquals("Draft", state.name)
            assertEquals("Unsaved note", state.note)
            assertEquals("Original", repository.pin!!.name)
            assertEquals(0, repository.updateCalls)
        }

    @Test
    fun `a place deleted while editing is not recreated`() =
        runTest {
            val repository = FakeRepository()
            val viewModel = editor(repository)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeNote("Keep this draft")
            repository.pin = null
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value as EditPinUiState.Content
            assertTrue(state.noLongerExists)
            assertEquals("Keep this draft", state.note)
            assertEquals(null, repository.pin)
        }

    private fun editor(repository: FakeRepository, handle: SavedStateHandle = SavedStateHandle()) =
        EditPinViewModel(repository, UpdatePinDetailsUseCase(repository) { 5000L }, handle)

    private class FakeRepository : PinRepository {
        var pin: Pin? = Pin(
            id = PinId("edit-test"),
            position = GeoPoint(10.0, 20.0),
            categoryId = SystemCategoryIds.PARKING,
            accuracyMeters = 8f,
            altitudeMeters = 30.0,
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
            name = "Original",
            note = "Original note",
        )
        var fail = false
        var updateCalls = 0
        var completion: CompletableDeferred<Unit>? = null

        override fun observePins(): Flow<List<Pin>> = MutableStateFlow(listOfNotNull(pin))

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(pin?.takeIf { it.id == pinId })

        override suspend fun savePin(pin: Pin) = error("Editor must never insert")

        override suspend fun deletePin(pinId: PinId) = error("Editor must never delete")

        override suspend fun updatePinDetails(pinId: PinId, name: String?, note: String?, updatedAtEpochMillis: Long): Boolean {
            updateCalls++
            completion?.await()
            if (fail) throw IOException("storage unavailable")
            val current = pin?.takeIf { it.id == pinId } ?: return false
            pin = current.copy(name = name, note = note, updatedAtEpochMillis = updatedAtEpochMillis)
            return true
        }
    }
}
