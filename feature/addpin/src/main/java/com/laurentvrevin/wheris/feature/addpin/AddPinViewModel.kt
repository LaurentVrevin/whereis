package com.laurentvrevin.wheris.feature.addpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.usecase.GetCurrentLocationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPinViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPinUiState>(AddPinUiState.PermissionRequired)
    val uiState: StateFlow<AddPinUiState> = _uiState.asStateFlow()

    private var activeJob: Job? = null
    private var isFineLocationGranted: Boolean = false
    private var isCoarseLocationGranted: Boolean = false

    fun onPermissionGranted(
        isFineGranted: Boolean,
        isCoarseGranted: Boolean,
    ) {
        this.isFineLocationGranted = isFineGranted
        this.isCoarseLocationGranted = isCoarseGranted
        fetchLocation()
    }

    fun onPermissionDenied(isPermanentlyDenied: Boolean = false) {
        activeJob?.cancel()
        activeJob = null
        _uiState.value = AddPinUiState.PermissionDenied(isPermanentlyDenied)
    }

    fun retryLocation() {
        if (isFineLocationGranted || isCoarseLocationGranted) {
            fetchLocation()
        } else {
            _uiState.value = AddPinUiState.PermissionRequired
        }
    }

    fun cancelLocation() {
        activeJob?.cancel()
        activeJob = null
        if (_uiState.value is AddPinUiState.Searching) {
            _uiState.value = AddPinUiState.PermissionRequired
        }
    }

    private fun fetchLocation() {
        activeJob?.cancel()
        _uiState.value = AddPinUiState.Searching

        activeJob =
            viewModelScope.launch {
                val result = getCurrentLocationUseCase()
                _uiState.value =
                    when (result) {
                        is LocationResult.Success -> {
                            val isApproximate = !isFineLocationGranted && isCoarseLocationGranted
                            AddPinUiState.Success(
                                location = result.location,
                                isApproximate = isApproximate,
                            )
                        }
                        is LocationResult.ServicesDisabled -> AddPinUiState.ServicesDisabled
                        is LocationResult.Timeout -> AddPinUiState.Timeout
                        is LocationResult.Error -> AddPinUiState.Error(result.cause?.message)
                    }
            }
    }
}
