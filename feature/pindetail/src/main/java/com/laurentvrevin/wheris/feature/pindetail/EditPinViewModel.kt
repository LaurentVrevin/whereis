package com.laurentvrevin.wheris.feature.pindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPinViewModel(
    private val pinRepository: PinRepository,
    private val categoryRepository: CategoryRepository,
    private val updatePin: UpdatePinUseCase,
    private val photoStorage: PhotoStorage,
    private val savedStateHandle: SavedStateHandle,
    private val cleanupScope: CoroutineScope,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditPinUiState>(EditPinUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var loadedId: PinId? = null
    private var loading = false
    private var abandoned = false
    private var categoryJob: Job? = null
    private var saveJob: Job? = null
    private var promoted: Pair<PhotoDraftReference, PhotoReference>? = null
    var cameraDraft: PhotoDraftReference? = null
        private set

    fun load(pinId: PinId) {
        if (abandoned || loading || (loadedId == pinId && _uiState.value != EditPinUiState.LoadFailed)) return
        loading = true
        loadedId = pinId
        _uiState.value = EditPinUiState.Loading
        viewModelScope.launch {
            try {
                val pin = pinRepository.observePin(pinId).first()
                if (abandoned) return@launch
                if (pin == null) {
                    _uiState.value = EditPinUiState.NotFound
                    return@launch
                }
                val categories = categoryRepository.observeCategories().first()
                check(categories.any { it.id == pin.categoryId })
                if (abandoned) return@launch
                val restore = savedStateHandle.get<String>(DRAFT_ID) == pinId.value
                val state =
                    EditPinUiState.Content(
                        pinId,
                        if (restore) savedStateHandle.get<String>(DRAFT_NAME).orEmpty() else pin.name.orEmpty(),
                        if (restore) savedStateHandle.get<String>(DRAFT_NOTE).orEmpty() else pin.note.orEmpty(),
                        categories,
                        if (restore) savedStateHandle.get<String>(DRAFT_CATEGORY)?.let(::CategoryId) ?: pin.categoryId else pin.categoryId,
                        if (restore) savedStateHandle.get<Boolean>(DRAFT_FAVORITE) ?: pin.isFavorite else pin.isFavorite,
                        pin.photoReference,
                    )
                _uiState.value = state
                rememberDraft(state)
                reloadCategories()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                if (!abandoned) _uiState.value = EditPinUiState.LoadFailed
            } finally {
                loading = false
            }
        }
    }

    fun reloadCategories() {
        if (abandoned || _uiState.value !is EditPinUiState.Content) return
        categoryJob?.cancel()
        categoryJob =
            viewModelScope.launch {
                try {
                    categoryRepository.observeCategories().collect { categories ->
                        val current = _uiState.value as? EditPinUiState.Content ?: return@collect
                        _uiState.value = current.copy(categories = categories, categoriesFailed = false)
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    val current = _uiState.value as? EditPinUiState.Content ?: return@launch
                    _uiState.value = current.copy(categoriesFailed = true)
                }
            }
    }

    fun changeName(name: String) = edit { it.copy(name = name) }

    fun changeNote(note: String) = edit { it.copy(note = note) }

    fun changeFavorite(favorite: Boolean) = edit { it.copy(isFavorite = favorite) }

    fun selectCategory(id: CategoryId) =
        edit { current ->
            if (current.categories.any { it.id == id }) current.copy(selectedCategoryId = id) else current
        }

    private fun edit(change: (EditPinUiState.Content) -> EditPinUiState.Content) {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (abandoned || current.isSaving || current.saveError == PinUpdateResult.PIN_NOT_FOUND) return
        val next = change(current).copy(saveError = null)
        _uiState.value = next
        rememberDraft(next)
    }

    fun beginPhotoAcquisition(): Boolean {
        val current = _uiState.value as? EditPinUiState.Content ?: return false
        if (abandoned || current.isSaving || current.isAcquiringPhoto || current.saveError == PinUpdateResult.PIN_NOT_FOUND) return false
        _uiState.value = current.copy(isAcquiringPhoto = true, photoFailed = false)
        return true
    }

    fun cameraPrepared(draft: PhotoDraftReference): Boolean {
        val current = _uiState.value as? EditPinUiState.Content
        if (abandoned || current?.isAcquiringPhoto != true) {
            discardLater(draft)
            return false
        }
        cameraDraft = draft
        return true
    }

    fun photoReady(draft: PhotoDraftReference) {
        val current = _uiState.value as? EditPinUiState.Content
        if (abandoned || current == null || !current.isAcquiringPhoto) {
            discardLater(draft)
            return
        }
        val previous = current.photo as? EditPhotoState.Replacement
        if (previous?.draft != draft) previous?.draft?.let(::discardLater)
        cameraDraft = null
        promoted = null
        _uiState.value =
            current.copy(
                photo = EditPhotoState.Replacement(draft, current.photo == EditPhotoState.Removed || previous?.originalRemoved == true),
                isAcquiringPhoto = false, photoFailed = false, saveError = null,
            )
    }

    fun photoCancelled() {
        cameraDraft?.let(::discardLater)
        cameraDraft = null
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (!abandoned) _uiState.value = current.copy(isAcquiringPhoto = false)
    }

    fun photoAcquisitionFailed() {
        photoCancelled()
        previewFailed()
    }

    fun previewFailed() {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (!abandoned) _uiState.value = current.copy(photoFailed = true)
    }

    fun removePhoto() {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (abandoned || current.isSaving || current.isAcquiringPhoto) return
        val replacement = current.photo as? EditPhotoState.Replacement
        if (replacement != null) {
            promoted = null
            _uiState.value = current.copy(isAcquiringPhoto = true)
            viewModelScope.launch {
                try {
                    photoStorage.discard(replacement.draft)
                    if (!abandoned) {
                        _uiState.value =
                            (_uiState.value as? EditPinUiState.Content ?: current).copy(
                                photo = if (replacement.originalRemoved) EditPhotoState.Removed else EditPhotoState.Unchanged,
                                isAcquiringPhoto = false, photoFailed = false, saveError = null,
                            )
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    if (!abandoned) {
                        _uiState.value =
                            (_uiState.value as? EditPinUiState.Content ?: current).copy(
                                isAcquiringPhoto = false, photoFailed = true,
                            )
                    }
                }
            }
        } else {
            edit { it.copy(photo = EditPhotoState.Removed, photoFailed = false) }
        }
    }

    fun save() {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (abandoned || !current.canSave || saveJob?.isActive == true) return
        _uiState.value = current.copy(isSaving = true, saveError = null)
        saveJob =
            viewModelScope.launch {
                try {
                    // Finish promotion/commit/handoff before abandonment can discard a replacement.
                    withContext(NonCancellable) {
                        val change =
                            when (val photo = current.photo) {
                                EditPhotoState.Unchanged -> PinPhotoChange.Keep
                                EditPhotoState.Removed -> PinPhotoChange.Remove
                                is EditPhotoState.Replacement -> {
                                    val reference =
                                        promoted?.takeIf { it.first == photo.draft }?.second
                                            ?: photoStorage.promote(photo.draft).also { promoted = photo.draft to it }
                                    PinPhotoChange.Replace(reference)
                                }
                            }
                        val result =
                            updatePin(
                                PinUpdate(
                                    current.pinId, current.selectedCategoryId, current.name,
                                    current.note, current.isFavorite, change,
                                ),
                            )
                        if (result == PinUpdateResult.SUCCESS || result == PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING) {
                            savedStateHandle.keys().filter { it.startsWith("editPin.") }.forEach { savedStateHandle.remove<Any?>(it) }
                            promoted = null
                            categoryJob?.cancel()
                            _uiState.value = EditPinUiState.Saved
                        } else {
                            _uiState.value =
                                (_uiState.value as? EditPinUiState.Content ?: current).copy(
                                    isSaving = false, saveError = result,
                                )
                            if (result == PinUpdateResult.CATEGORY_NOT_FOUND && !abandoned) reloadCategories()
                        }
                    }
                } catch (exception: CancellationException) {
                    val latest = _uiState.value as? EditPinUiState.Content
                    if (latest != null && !abandoned) _uiState.value = latest.copy(isSaving = false)
                    throw exception
                } catch (_: Exception) {
                    if (!abandoned) {
                        _uiState.value =
                            (_uiState.value as? EditPinUiState.Content ?: current).copy(
                                isSaving = false, saveError = PinUpdateResult.TECHNICAL_FAILURE,
                            )
                    }
                }
            }
    }

    fun abandon() {
        if (abandoned || _uiState.value == EditPinUiState.Saved) return
        abandoned = true
        categoryJob?.cancel()
        val drafts =
            listOfNotNull(
                (_uiState.value as? EditPinUiState.Content)?.photo?.let {
                    (it as? EditPhotoState.Replacement)?.draft
                },
                cameraDraft,
            ).distinct()
        cleanupScope.launch {
            saveJob?.join()
            if (_uiState.value != EditPinUiState.Saved) drafts.forEach { discardSafely(it) }
        }
    }

    private fun discardLater(draft: PhotoDraftReference) {
        cleanupScope.launch { discardSafely(draft) }
    }

    private suspend fun discardSafely(draft: PhotoDraftReference) {
        try {
            photoStorage.abandon(draft)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            previewFailed()
        }
    }

    private fun rememberDraft(state: EditPinUiState.Content) {
        savedStateHandle[DRAFT_ID] = state.pinId.value
        savedStateHandle[DRAFT_NAME] = state.name
        savedStateHandle[DRAFT_NOTE] = state.note
        savedStateHandle[DRAFT_CATEGORY] = state.selectedCategoryId.value
        savedStateHandle[DRAFT_FAVORITE] = state.isFavorite
    }

    override fun onCleared() {
        abandon()
        super.onCleared()
    }

    private companion object {
        const val DRAFT_ID = "editPin.id"
        const val DRAFT_NAME = "editPin.name"
        const val DRAFT_NOTE = "editPin.note"
        const val DRAFT_CATEGORY = "editPin.category"
        const val DRAFT_FAVORITE = "editPin.favorite"
    }
}
