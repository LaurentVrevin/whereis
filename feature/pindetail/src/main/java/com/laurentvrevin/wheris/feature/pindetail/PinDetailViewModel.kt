package com.laurentvrevin.wheris.feature.pindetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.model.bearingTo
import com.laurentvrevin.wheris.core.model.distanceTo
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PinDetailViewModel(
    private val pinRepository: PinRepository,
    private val userLocationRepository: UserLocationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PinDetailUiState>(PinDetailUiState.Loading)
    val uiState: StateFlow<PinDetailUiState> = _uiState.asStateFlow()

    private var pinJob: Job? = null
    private var locationJob: Job? = null
    private var currentLocation: UserLocation? = null

    fun observePin(pinId: PinId) {
        pinJob?.cancel()
        locationJob?.cancel()
        currentLocation = null
        _uiState.value = PinDetailUiState.Loading

        pinJob =
            viewModelScope.launch {
                try {
                    pinRepository.observePin(pinId).collect { pin ->
                        _uiState.value =
                            if (pin == null) {
                                PinDetailUiState.NotFound
                            } else {
                                pin.toContent(currentLocation)
                            }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    _uiState.value = PinDetailUiState.Error
                }
            }
    }

    fun requestCurrentLocation() {
        if (locationJob?.isActive == true) return

        locationJob =
            viewModelScope.launch {
                val location =
                    try {
                        when (val result = userLocationRepository.getCurrentLocation()) {
                            is LocationResult.Success -> result.location
                            LocationResult.ServicesDisabled,
                            LocationResult.Timeout,
                            LocationResult.TechnicalError,
                            -> null
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        null
                    }

                currentLocation = location

                val current = _uiState.value
                if (current is PinDetailUiState.Content) {
                    _uiState.value = current.pin.toContent(location)
                }
            }
    }

    override fun onCleared() {
        pinJob?.cancel()
        locationJob?.cancel()
        super.onCleared()
    }

    private fun Pin.toContent(userLocation: UserLocation?): PinDetailUiState.Content {
        if (userLocation == null) {
            return PinDetailUiState.Content(pin = this)
        }

        val distance = userLocation.position.distanceTo(position)
        val direction =
            if (distance > MIN_DIRECTION_DISTANCE_METERS) {
                CardinalDirection.fromBearing(userLocation.position.bearingTo(position))
            } else {
                null
            }

        return PinDetailUiState.Content(
            pin = this,
            distanceMeters = distance,
            cardinalDirection = direction,
        )
    }

    private companion object {
        const val MIN_DIRECTION_DISTANCE_METERS = 1.0
    }
}
