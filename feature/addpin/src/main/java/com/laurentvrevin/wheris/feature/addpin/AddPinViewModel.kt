package com.laurentvrevin.wheris.feature.addpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPinViewModel(
    private val userLocationRepository: UserLocationRepository,
    private val categoryRepository: CategoryRepository,
    private val createPinUseCase: CreatePinUseCase,
    private val photoStorage: PhotoStorage,
    private val cleanupScope: CoroutineScope,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPinUiState>(AddPinUiState.PermissionRequired)
    val uiState: StateFlow<AddPinUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null
    private var categoryJob: Job? = null
    private var saveJob: Job? = null
    private var pendingCreatedCategory: Category? = null
    private var isFineLocationGranted: Boolean = false
    private var isCoarseLocationGranted: Boolean = false
    private var acceptedLocation: UserLocation? = null
    private var acceptedLocationIsApproximate: Boolean = false
    private var retainedSelection: AddPinUiState.CategorySelection? = null
    private var abandoned = false
    private var promotedPhoto: Pair<PhotoDraftReference, PhotoReference>? = null
    var cameraDraft: PhotoDraftReference? = null
        private set

    private fun selection(): AddPinUiState.CategorySelection? =
        when (val current = _uiState.value) {
            is AddPinUiState.CategorySelection -> current
            is AddPinUiState.Details -> current.selection
            is AddPinUiState.CategoryCreation -> current.selection
            else -> retainedSelection
        }

    fun openDetails() {
        val current = _uiState.value as? AddPinUiState.CategorySelection ?: return
        if (!current.isSaving && current.categories.any { it.id == current.selectedCategoryId }) {
            _uiState.value = AddPinUiState.Details(current)
        }
    }

    fun backFromDetails() {
        val current = _uiState.value as? AddPinUiState.Details ?: return
        if (!current.selection.isSaving && !current.selection.details.isAcquiringPhoto) _uiState.value = current.selection
    }

    private fun editDetails(transform: (PlaceDetailsDraft) -> PlaceDetailsDraft) {
        val current = selection() ?: return
        if (!current.isSaving && !abandoned) {
            updateSelection { it.copy(details = transform(it.details), saveFailed = false) }
        }
    }

    fun updateName(name: String) = editDetails { it.copy(name = name) }

    fun updateNote(note: String) = editDetails { it.copy(note = note) }

    fun updateFavorite(favorite: Boolean) = editDetails { it.copy(isFavorite = favorite) }

    fun beginPhotoAcquisition(): Boolean {
        val current = _uiState.value as? AddPinUiState.Details ?: return false
        if (current.selection.isSaving || current.selection.details.isAcquiringPhoto || abandoned) return false
        editDetails { it.copy(isAcquiringPhoto = true, photoFailed = false) }
        return true
    }

    fun onCameraPrepared(draft: PhotoDraftReference): Boolean {
        if (abandoned || selection()?.details?.isAcquiringPhoto != true) {
            discardLater(draft)
            return false
        }
        cameraDraft = draft
        return true
    }

    fun photoCancelled() {
        cameraDraft?.let { draft -> discardLater(draft) }
        cameraDraft = null
        editDetails { it.copy(isAcquiringPhoto = false, photoFailed = false) }
    }

    fun photoFailed() {
        // A preview/cleanup failure invalidates the cached handoff; storage must revalidate on retry.
        promotedPhoto = null
        cameraDraft?.let { draft -> discardLater(draft) }
        cameraDraft = null
        editDetails { it.copy(isAcquiringPhoto = false, photoFailed = true) }
    }

    fun photoReady(draft: PhotoDraftReference) {
        cameraDraft = null
        if (abandoned || _uiState.value is AddPinUiState.Saved || selection() == null) {
            discardLater(draft)
            return
        }
        val previous = selection()?.details?.photo
        editDetails { it.copy(photo = draft, isAcquiringPhoto = false, photoFailed = false) }
        if (previous != null && previous != draft) discardLater(previous)
    }

    fun removePhoto() {
        val current = selection() ?: return
        if (current.isSaving || current.details.isAcquiringPhoto) return
        val photo = current.details.photo ?: return
        promotedPhoto = null
        editDetails { it.copy(isAcquiringPhoto = true) }
        viewModelScope.launch {
            try {
                photoStorage.discard(photo)
                editDetails { it.copy(photo = null, isAcquiringPhoto = false, photoFailed = false) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                editDetails { it.copy(isAcquiringPhoto = false, photoFailed = true) }
            }
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
            // Owned cache/pending files remain discoverable by next-process reconciliation.
            editDetails { it.copy(photoFailed = true) }
        }
    }

    fun abandon() {
        if (abandoned || _uiState.value is AddPinUiState.Saved) return
        abandoned = true
        val photos = listOfNotNull(selection()?.details?.photo, cameraDraft).distinct()
        cleanupScope.launch {
            // A save already in progress must finish before its unattached file can be removed.
            saveJob?.join()
            photos.forEach { discardSafely(it) }
        }
    }

    override fun onCleared() {
        abandon()
        super.onCleared()
    }

    fun onPermissionGranted(
        isFineGranted: Boolean,
        isCoarseGranted: Boolean,
    ) {
        isFineLocationGranted = isFineGranted
        isCoarseLocationGranted = isCoarseGranted

        when (_uiState.value) {
            is AddPinUiState.PermissionRequired,
            is AddPinUiState.PermissionDenied,
            -> fetchLocation()

            else -> Unit
        }
    }

    fun onPermissionDenied(isPermanentlyDenied: Boolean = false) {
        isFineLocationGranted = false
        isCoarseLocationGranted = false
        locationJob?.cancel()
        locationJob = null
        _uiState.value = AddPinUiState.PermissionDenied(isPermanentlyDenied)
    }

    fun retryLocation(
        isFineGranted: Boolean = isFineLocationGranted,
        isCoarseGranted: Boolean = isCoarseLocationGranted,
    ) {
        when (_uiState.value) {
            is AddPinUiState.CategorySelection,
            is AddPinUiState.CategoryCreation,
            is AddPinUiState.Details,
            is AddPinUiState.Saved,
            -> return
            else -> Unit
        }
        isFineLocationGranted = isFineGranted
        isCoarseLocationGranted = isCoarseGranted
        if (isFineLocationGranted || isCoarseLocationGranted) {
            fetchLocation()
        } else {
            _uiState.value = AddPinUiState.PermissionRequired
        }
    }

    fun confirmPosition() {
        val state = _uiState.value as? AddPinUiState.PositionFound ?: return
        acceptedLocation = state.location
        acceptedLocationIsApproximate = state.isApproximate
        observeCategories(state.location)
    }

    fun selectCategory(categoryId: CategoryId) {
        val state = _uiState.value as? AddPinUiState.CategorySelection ?: return
        if (state.isSaving) return
        if (state.categories.none { it.id == categoryId }) return

        _uiState.value =
            state.copy(
                selectedCategoryId = categoryId,
                saveFailed = false,
            )
    }

    fun openCategoryCreation() {
        val selection = _uiState.value as? AddPinUiState.CategorySelection ?: return
        if (selection.isSaving || selection.isLoadingCategories || selection.categoryLoadFailed) return
        _uiState.value = AddPinUiState.CategoryCreation(selection)
    }

    fun updateCategoryName(name: String) = updateCreation { it.copy(name = name, creationFailed = false) }

    fun selectCategoryIcon(key: CategoryIconKey) = updateCreation { it.copy(iconKey = key, creationFailed = false) }

    fun selectCategoryColor(key: CategoryColorKey) = updateCreation { it.copy(colorKey = key, creationFailed = false) }

    private fun updateCreation(transform: (AddPinUiState.CategoryCreation) -> AddPinUiState.CategoryCreation) {
        val state = _uiState.value as? AddPinUiState.CategoryCreation ?: return
        if (!state.isCreating) _uiState.value = transform(state)
    }

    fun cancelCategoryCreation() {
        val state = _uiState.value as? AddPinUiState.CategoryCreation ?: return
        if (!state.isCreating) _uiState.value = state.selection
    }

    fun createCategory() {
        val state = _uiState.value as? AddPinUiState.CategoryCreation ?: return
        if (!state.canCreate) return
        _uiState.value = state.copy(isCreating = true, creationFailed = false)
        viewModelScope.launch {
            try {
                val category = categoryRepository.createCustomCategory(state.name.trim(), state.iconKey, state.colorKey)
                val current = _uiState.value as? AddPinUiState.CategoryCreation ?: return@launch
                // The insert may complete before Room emits. Bridge that interval with the returned row.
                pendingCreatedCategory = category.takeUnless { created -> current.selection.categories.any { it.id == created.id } }
                _uiState.value =
                    current.selection.copy(
                        categories = (current.selection.categories + category).distinctBy { it.id },
                        selectedCategoryId = category.id,
                        isLoadingCategories = false,
                        saveFailed = false,
                    )
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                val current = _uiState.value as? AddPinUiState.CategoryCreation ?: return@launch
                _uiState.value = current.copy(isCreating = false, creationFailed = true)
            }
        }
    }

    fun savePin() {
        if (_uiState.value !is AddPinUiState.CategorySelection && _uiState.value !is AddPinUiState.Details) return
        val state = selection() ?: return
        val categoryId = state.selectedCategoryId ?: return
        if (state.isSaving || saveJob?.isActive == true || state.details.isAcquiringPhoto || abandoned) return
        if (state.categories.none { it.id == categoryId }) return

        updateSelection { it.copy(isSaving = true, saveFailed = false) }
        saveJob =
            viewModelScope.launch {
                try {
                    val pin =
                        withContext(NonCancellable) {
                            val photo =
                                state.details.photo?.let { draft ->
                                    promotedPhoto?.takeIf { it.first == draft }?.second
                                        ?: photoStorage.promote(draft).also { promotedPhoto = draft to it }
                                }
                            createPinUseCase(
                                state.location, categoryId, state.details.name, state.details.note,
                                state.details.isFavorite, photo,
                            )
                        }
                    categoryJob?.cancel()
                    categoryJob = null
                    _uiState.value = AddPinUiState.Saved(pin)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    updateSelection { it.copy(isSaving = false, saveFailed = true) }
                }
            }
    }

    fun backToPosition() {
        val state = _uiState.value as? AddPinUiState.CategorySelection ?: return
        if (state.isSaving) return
        retainedSelection = state
        val location = acceptedLocation ?: return
        categoryJob?.cancel()
        categoryJob = null
        _uiState.value =
            AddPinUiState.PositionFound(
                location = location,
                isApproximate = acceptedLocationIsApproximate,
            )
    }

    fun cancelLocation() {
        locationJob?.cancel()
        locationJob = null
        if (_uiState.value is AddPinUiState.Searching) {
            _uiState.value = AddPinUiState.PermissionRequired
        }
    }

    private fun fetchLocation() {
        locationJob?.cancel()
        _uiState.value = AddPinUiState.Searching

        locationJob =
            viewModelScope.launch {
                val result = userLocationRepository.getCurrentLocation()
                _uiState.value =
                    when (result) {
                        is LocationResult.Success -> {
                            val isApproximate = !isFineLocationGranted && isCoarseLocationGranted
                            AddPinUiState.PositionFound(
                                location = result.location,
                                isApproximate = isApproximate,
                            )
                        }

                        is LocationResult.ServicesDisabled -> AddPinUiState.ServicesDisabled
                        is LocationResult.Timeout -> AddPinUiState.Timeout
                        is LocationResult.TechnicalError -> AddPinUiState.TechnicalError
                    }
            }
    }

    private fun observeCategories(location: UserLocation) {
        categoryJob?.cancel()
        _uiState.value =
            retainedSelection?.copy(location = location, isLoadingCategories = true) ?: AddPinUiState.CategorySelection(
                location = location,
                isLoadingCategories = true,
            )

        categoryJob =
            viewModelScope.launch {
                try {
                    categoryRepository.observeCategories().collect { categories ->
                        pendingCreatedCategory?.let { pending ->
                            if (categories.any { it.id == pending.id }) pendingCreatedCategory = null
                        }
                        val available = (categories + listOfNotNull(pendingCreatedCategory)).distinctBy { it.id }
                        updateSelection { it.copy(categories = available, isLoadingCategories = false, categoryLoadFailed = false) }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    updateSelection { it.copy(isLoadingCategories = false, categoryLoadFailed = true) }
                }
            }
    }

    private fun updateSelection(transform: (AddPinUiState.CategorySelection) -> AddPinUiState.CategorySelection) {
        _uiState.value =
            when (val current = _uiState.value) {
                is AddPinUiState.CategorySelection -> transform(current)
                is AddPinUiState.CategoryCreation -> current.copy(selection = transform(current.selection))
                is AddPinUiState.Details -> current.copy(selection = transform(current.selection))
                else -> current
            }
    }
}
