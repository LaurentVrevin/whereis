package com.laurentvrevin.wheris.feature.pindetail

import androidx.lifecycle.SavedStateHandle
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
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
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, failed.saveError)
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
            assertEquals(PinUpdateResult.PIN_NOT_FOUND, state.saveError)
            assertEquals("Keep this draft", state.note)
            assertEquals(null, repository.pin)
        }

    private fun editor(
        repository: FakeRepository,
        handle: SavedStateHandle = SavedStateHandle(),
        photos: EditPhotos = EditPhotos(),
        categories: EditCategories = EditCategories(),
    ) = EditPinViewModel(repository, categories, UpdatePinUseCase(repository) { 5000L }, photos, handle, CoroutineScope(dispatcher))

    @Test
    fun `committed update with pending cleanup finishes the existing editor`() =
        runTest {
            val repository = FakeRepository()
            repository.updateResult = PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING
            val handle = SavedStateHandle()
            val viewModel = editor(repository, handle)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeName("Saved name")
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPinUiState.Saved, viewModel.uiState.value)
            assertEquals("Saved name", repository.pin!!.name)
            assertTrue(handle.keys().isEmpty())
        }

    @Test
    fun `missing category is a recoverable save error and retains the draft`() =
        runTest {
            val repository = FakeRepository()
            repository.updateResult = PinUpdateResult.CATEGORY_NOT_FOUND
            val viewModel = editor(repository)
            viewModel.load(repository.pin!!.id)
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.changeName("Draft")
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()
            val failed = viewModel.uiState.value as EditPinUiState.Content
            assertEquals(PinUpdateResult.CATEGORY_NOT_FOUND, failed.saveError)
            assertEquals("Draft", failed.name)
            assertEquals("Original", repository.pin!!.name)
        }

    private fun loaded(
        repository: FakeRepository = FakeRepository(),
        photos: EditPhotos = EditPhotos(),
        categories: EditCategories = EditCategories(),
    ): EditPinViewModel =
        editor(repository, photos = photos, categories = categories).also {
            it.load(repository.pin?.id ?: PinId("edit-test"))
            dispatcher.scheduler.advanceUntilIdle()
        }

    private fun replace(
        viewModel: EditPinViewModel,
        id: String = "draft-b",
    ) {
        assertTrue(viewModel.beginPhotoAcquisition())
        viewModel.photoReady(PhotoDraftReference(id))
    }

    private fun content(viewModel: EditPinViewModel) = viewModel.uiState.value as EditPinUiState.Content

    @Test fun `full loading includes dynamic categories favorite and original photo`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = repository.pin!!.copy(isFavorite = true, photoReference = PhotoReference("original"))
            val categories = EditCategories()
            categories.categories.value +=
                Category(
                    CategoryId("custom"), false, "Mon lieu", com.laurentvrevin.wheris.core.model.CategoryIconKey.PLACE,
                    com.laurentvrevin.wheris.core.model.CategoryColorKey.ORANGE, 1000L,
                )
            val state = content(loaded(repository, categories = categories))
            assertEquals(repository.pin!!.categoryId, state.selectedCategoryId)
            assertEquals(true, state.isFavorite)
            assertEquals(PhotoReference("original"), state.originalPhoto)
            assertEquals(EditPhotoState.Unchanged, state.photo)
            assertEquals(3, state.categories.size)
        }

    @Test fun `category and favorite changes submit through the canonical update`() =
        runTest {
            val repository = FakeRepository()
            val original = repository.pin!!
            val vm = loaded(repository)
            vm.selectCategory(SystemCategoryIds.OTHER)
            vm.changeFavorite(true)
            vm.changeNote("  Useful\n note  ")
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                original.copy(
                    categoryId = SystemCategoryIds.OTHER,
                    isFavorite = true,
                    note = "  Useful\n note  ",
                    updatedAtEpochMillis = 5000L,
                ),
                repository.pin,
            )
        }

    @Test fun `category emissions retain raw text favorite and photo draft`() =
        runTest {
            val categories = EditCategories()
            val vm = loaded(categories = categories)
            vm.changeName("  Raw  ")
            vm.changeFavorite(true)
            replace(vm)
            categories.categories.value = categories.categories.value.reversed()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals("  Raw  ", content(vm).name)
            assertEquals(true, content(vm).isFavorite)
            assertEquals(EditPhotoState.Replacement(PhotoDraftReference("draft-b")), content(vm).photo)
        }

    @Test fun `keep does not promote or discard photos`() =
        runTest {
            val photos = EditPhotos()
            val vm = loaded(photos = photos)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
            assertEquals(0, photos.promotions)
            assertTrue(photos.discarded.isEmpty())
        }

    @Test fun `removing original is only an intention until save and cancel keeps it`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = repository.pin!!.copy(photoReference = PhotoReference("original"))
            val original = repository.pin!!
            val photos = EditPhotos()
            val vm = loaded(repository, photos)
            vm.removePhoto()
            assertEquals(EditPhotoState.Removed, content(vm).photo)
            vm.abandon()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(original, repository.pin)
            assertTrue(photos.discarded.isEmpty())
        }

    @Test fun `remove save sends explicit removal`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = repository.pin!!.copy(photoReference = PhotoReference("original"))
            val vm = loaded(repository)
            vm.removePhoto()
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(null, repository.pin!!.photoReference)
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
        }

    @Test fun `picker replacement promotes then saves`() =
        runTest {
            val repository = FakeRepository()
            val photos = EditPhotos()
            val vm = loaded(repository, photos)
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PhotoReference("draft-b"), repository.pin!!.photoReference)
            assertEquals(1, photos.promotions)
            vm.abandon()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(photos.discarded.isEmpty())
        }

    @Test fun `camera neutral result converges to the same replacement path`() =
        runTest {
            val repository = FakeRepository()
            val vm = loaded(repository)
            assertTrue(vm.beginPhotoAcquisition())
            assertTrue(vm.cameraPrepared(PhotoDraftReference("capture")))
            vm.photoReady(PhotoDraftReference("capture"))
            assertEquals(null, vm.cameraDraft)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PhotoReference("capture"), repository.pin!!.photoReference)
        }

    @Test fun `replacing B with C only discards B`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = repository.pin!!.copy(photoReference = PhotoReference("original"))
            val photos = EditPhotos()
            val vm = loaded(repository, photos)
            replace(vm, "b")
            replace(vm, "c")
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf(PhotoDraftReference("b")), photos.discarded)
            assertEquals(EditPhotoState.Replacement(PhotoDraftReference("c")), content(vm).photo)
            assertEquals(PhotoReference("original"), repository.pin!!.photoReference)
        }

    @Test fun `removing replacement returns to original`() =
        runTest {
            val photos = EditPhotos()
            val vm = loaded(photos = photos)
            replace(vm)
            vm.removePhoto()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPhotoState.Unchanged, content(vm).photo)
            assertEquals(listOf(PhotoDraftReference("draft-b")), photos.discarded)
        }

    @Test fun `removing replacement preserves a prior original removal intention`() =
        runTest {
            val vm = loaded()
            vm.removePhoto()
            replace(vm, "b")
            replace(vm, "c")
            vm.removePhoto()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPhotoState.Removed, content(vm).photo)
        }

    @Test fun `picker cancellation retains the previous replacement and fields`() =
        runTest {
            val vm = loaded()
            vm.changeName("Raw")
            replace(vm)
            val before = content(vm)
            vm.beginPhotoAcquisition()
            vm.photoCancelled()
            assertEquals(before, content(vm))
        }

    @Test fun `camera cancellation cleans only the new target`() =
        runTest {
            val photos = EditPhotos()
            val vm = loaded(photos = photos)
            replace(vm, "b")
            val before = content(vm)
            vm.beginPhotoAcquisition()
            vm.cameraPrepared(PhotoDraftReference("capture"))
            vm.photoCancelled()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(before, content(vm))
            assertEquals(listOf(PhotoDraftReference("capture")), photos.discarded)
        }

    @Test fun `acquisition failure preserves all previous draft fields`() =
        runTest {
            val vm = loaded()
            replace(vm)
            val before = content(vm)
            vm.beginPhotoAcquisition()
            vm.photoAcquisitionFailed()
            assertEquals(before.copy(photoFailed = true), content(vm))
        }

    @Test fun `failed update retries without repeated promotion`() =
        runTest {
            val repository = FakeRepository()
            repository.updateResult = PinUpdateResult.TECHNICAL_FAILURE
            val photos = EditPhotos()
            val vm = loaded(repository, photos)
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, content(vm).saveError)
            assertEquals(EditPhotoState.Replacement(PhotoDraftReference("draft-b")), content(vm).photo)
            repository.updateResult = PinUpdateResult.SUCCESS
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, photos.promotions)
            assertEquals(2, repository.updateCalls)
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
        }

    @Test fun `back discards a promoted replacement after failed update`() =
        runTest {
            val repository = FakeRepository()
            repository.updateResult = PinUpdateResult.TECHNICAL_FAILURE
            val original = repository.pin!!
            val photos = EditPhotos()
            val vm = loaded(repository, photos)
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            vm.abandon()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf(PhotoDraftReference("draft-b")), photos.discarded)
            assertEquals(original, repository.pin)
        }

    @Test fun `promotion failure keeps draft and permits retry`() =
        runTest {
            val photos = EditPhotos()
            photos.promotionFailure = IOException("unavailable")
            val repository = FakeRepository()
            val vm = loaded(repository, photos)
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, content(vm).saveError)
            assertEquals(0, repository.updateCalls)
            photos.promotionFailure = null
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
        }

    @Test fun `double save is blocked during promotion and mutation`() =
        runTest {
            val photos = EditPhotos()
            photos.promotionCompletion = CompletableDeferred()
            val repository = FakeRepository()
            repository.completion = CompletableDeferred()
            val vm = loaded(repository, photos)
            replace(vm)
            vm.save()
            vm.save()
            dispatcher.scheduler.runCurrent()
            assertEquals(1, photos.promotions)
            assertEquals(0, repository.updateCalls)
            photos.promotionCompletion!!.complete(Unit)
            dispatcher.scheduler.runCurrent()
            vm.save()
            assertEquals(1, repository.updateCalls)
            repository.completion!!.complete(Unit)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
        }

    @Test fun `acquisition prevents save and a second acquisition`() =
        runTest {
            val repository = FakeRepository()
            val vm = loaded(repository)
            assertTrue(vm.beginPhotoAcquisition())
            assertEquals(false, vm.beginPhotoAcquisition())
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(0, repository.updateCalls)
        }

    @Test fun `photo already attached remains an explicit recoverable error`() =
        runTest {
            val repository = FakeRepository()
            repository.updateResult = PinUpdateResult.PHOTO_ALREADY_ATTACHED
            val vm = loaded(repository)
            vm.changeName("Draft")
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PinUpdateResult.PHOTO_ALREADY_ATTACHED, content(vm).saveError)
            assertEquals("Draft", content(vm).name)
            assertTrue(content(vm).hasPhoto)
        }

    @Test fun `missing pin loads not found`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = null
            assertEquals(EditPinUiState.NotFound, loaded(repository).uiState.value)
        }

    @Test fun `category load failure can retry without inventing data`() =
        runTest {
            val categories = EditCategories()
            categories.failure = IOException("unavailable")
            val vm = loaded(categories = categories)
            assertEquals(EditPinUiState.LoadFailed, vm.uiState.value)
            categories.failure = null
            vm.load(PinId("edit-test"))
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, content(vm).categories.size)
        }

    @Test fun `missing current category is a load failure`() =
        runTest {
            val categories = EditCategories()
            categories.categories.value = emptyList()
            assertEquals(EditPinUiState.LoadFailed, loaded(categories = categories).uiState.value)
        }

    @Test fun `category subscription failure retains draft and can reload`() =
        runTest {
            val categories = EditCategories()
            val vm = loaded(categories = categories)
            vm.changeName("Draft")
            categories.failure = IOException("unavailable")
            vm.reloadCategories()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(content(vm).categoriesFailed)
            assertEquals(false, content(vm).canSave)
            assertEquals("Draft", content(vm).name)
            categories.failure = null
            vm.reloadCategories()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(content(vm).canSave)
        }

    @Test fun `preview failure does not change persisted reference and keep can save`() =
        runTest {
            val repository = FakeRepository()
            repository.pin = repository.pin!!.copy(photoReference = PhotoReference("original"))
            val vm = loaded(repository)
            vm.previewFailed()
            assertEquals(EditPhotoState.Unchanged, content(vm).photo)
            assertTrue(content(vm).canSave)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(PhotoReference("original"), repository.pin!!.photoReference)
        }

    @Test fun `late acquisition after abandonment is discarded`() =
        runTest {
            val photos = EditPhotos()
            val vm = loaded(photos = photos)
            vm.beginPhotoAcquisition()
            vm.abandon()
            vm.photoReady(PhotoDraftReference("late"))
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf(PhotoDraftReference("late")), photos.discarded)
        }

    @Test fun `cancellation is not converted into technical failure`() =
        runTest {
            val photos = EditPhotos()
            photos.promotionFailure = CancellationException("cancelled")
            val vm = loaded(photos = photos)
            replace(vm)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(null, content(vm).saveError)
            assertEquals(false, content(vm).isSaving)
            photos.promotionFailure = null
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(EditPinUiState.Saved, vm.uiState.value)
        }

    private class FakeRepository : PinRepository {
        var pin: Pin? =
            Pin(
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
        var updateResult = PinUpdateResult.SUCCESS
        var updateCalls = 0
        var completion: CompletableDeferred<Unit>? = null

        override fun observePins(): Flow<List<Pin>> = MutableStateFlow(listOfNotNull(pin))

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(pin?.takeIf { it.id == pinId })

        override suspend fun savePin(pin: Pin) = error("Editor must never insert")

        override suspend fun deletePin(pinId: PinId) = error("Editor must never delete")

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult {
            updateCalls++
            completion?.await()
            if (fail) throw IOException("storage unavailable")
            val current = pin?.takeIf { it.id == update.pinId } ?: return PinUpdateResult.PIN_NOT_FOUND
            if (updateResult != PinUpdateResult.SUCCESS && updateResult != PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING) {
                return updateResult
            }
            pin =
                current.copy(
                    categoryId = update.categoryId,
                    name = update.name,
                    note = update.note,
                    isFavorite = update.isFavorite,
                    updatedAtEpochMillis = updatedAtEpochMillis,
                    photoReference =
                        when (val photo = update.photoChange) {
                            PinPhotoChange.Keep -> current.photoReference
                            PinPhotoChange.Remove -> null
                            is PinPhotoChange.Replace -> photo.photo
                        },
                )
            return updateResult
        }
    }
}
