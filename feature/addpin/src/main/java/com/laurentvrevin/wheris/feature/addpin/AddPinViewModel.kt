package com.laurentvrevin.wheris.feature.addpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPinViewModel(
    private val userLocationRepository: UserLocationRepository,
    private val categoryRepository: CategoryRepository,
    private val createPinUseCase: CreatePinUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPinUiState>(AddPinUiState.PermissionRequired)
    val uiState: StateFlow<AddPinUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null
    private var categoryJob: Job? = null
    private var saveJob: Job? = null
    private var isFineLocationGranted: Boolean = false
    private var isCoarseLocationGranted: Boolean = false
    private var acceptedLocation: UserLocation? = null
    private var acceptedLocationIsApproximate: Boolean = false

    fun onPermissionGranted(
        isFineGranted: Boolean,
        isCoarseGranted: Boolean,
    ) {
        isFineLocationGranted = isFineGranted
        isCoarseLocationGranted = isCoarseGranted

        when (_uiState.value) {
            is AddPinUiState.Searching,
            is AddPinUiState.PositionFound,
            is AddPinUiState.CategorySelection,
            is AddPinUiState.Saved,
            -> Unit

            else -> fetchLocation()
        }
    }

    fun onPermissionDenied(isPermanentlyDenied: Boolean = false) {
        locationJob?.cancel()
        locationJob = null
        _uiState.value = AddPinUiState.PermissionDenied(isPermanentlyDenied)
    }

    fun retryLocation() {
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

    fun savePin() {
        val state = _uiState.value as? AddPinUiState.CategorySelection ?: return
        val categoryId = state.selectedCategoryId ?: return
        if (state.isSaving || saveJob?.isActive == true) return

        _uiState.value = state.copy(isSaving = true, saveFailed = false)
        saveJob =
            viewModelScope.launch {
                try {
                    val pin = createPinUseCase(state.location, categoryId)
                    categoryJob?.cancel()
                    categoryJob = null
                    _uiState.value = AddPinUiState.Saved(pin)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    val current = _uiState.value as? AddPinUiState.CategorySelection ?: return@launch
                    _uiState.value = current.copy(isSaving = false, saveFailed = true)
                }
            }
    }

    fun backToPosition() {
        val location = acceptedLocation ?: return
        saveJob?.cancel()
        saveJob = null
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
            AddPinUiState.CategorySelection(
                location = location,
                isLoadingCategories = true,
            )

        categoryJob =
            viewModelScope.launch {
                try {
                    categoryRepository.observeSystemCategories().collect { categories ->
                        val current = _uiState.value as? AddPinUiState.CategorySelection ?: return@collect
                        _uiState.value =
                            current.copy(
                                categories = categories,
                                isLoadingCategories = false,
                                categoryLoadFailed = false,
                            )
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    val current = _uiState.value as? AddPinUiState.CategorySelection ?: return@launch
                    _uiState.value =
                        current.copy(
                            isLoadingCategories = false,
                            categoryLoadFailed = true,
                        )
                }
            }
    }
}
