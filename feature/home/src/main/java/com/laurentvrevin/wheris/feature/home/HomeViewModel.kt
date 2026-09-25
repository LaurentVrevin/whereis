package com.laurentvrevin.wheris.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    pinRepository: PinRepository,
    private val userLocationRepository: UserLocationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                pinRepository.observePins().collect { pins ->
                    val currentSelected = _uiState.value.selectedPinId
                    val validSelected =
                        if (currentSelected != null && pins.any { it.id == currentSelected }) {
                            currentSelected
                        } else {
                            null
                        }
                    _uiState.value =
                        _uiState.value.copy(
                            pins = pins,
                            selectedPinId = validSelected,
                            userLocation = if (validSelected == null) null else _uiState.value.userLocation,
                        )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(dataUnavailable = true)
            }
        }
    }

    fun onSavedPlaceSelected(pinId: PinId) {
        _uiState.value = _uiState.value.copy(selectedPinId = pinId)
        viewModelScope.launch {
            try {
                when (val result = userLocationRepository.getCurrentLocation()) {
                    is LocationResult.Success -> {
                        if (_uiState.value.selectedPinId == pinId) {
                            _uiState.value = _uiState.value.copy(userLocation = result.location)
                        }
                    }
                    LocationResult.ServicesDisabled,
                    LocationResult.Timeout,
                    LocationResult.TechnicalError,
                    -> {}
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {}
        }
    }

    fun onMapSelectionCleared() {
        _uiState.value =
            _uiState.value.copy(
                selectedPinId = null,
                userLocation = null,
            )
    }
}
