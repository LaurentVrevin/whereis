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
    private var observedPinId: PinId? = null

    fun observePin(pinId: PinId) {
        if (observedPinId == pinId && (pinJob?.isActive == true || _uiState.value == PinDetailUiState.Deleted)) return
        observedPinId = pinId
        pinJob?.cancel()
        locationJob?.cancel()
        currentLocation = null
        _uiState.value = PinDetailUiState.Loading

        pinJob =
            viewModelScope.launch {
                try {
                    pinRepository.observePin(pinId).collect { pin ->
                        val current = _uiState.value
                        // Room may emit null before deletePin returns. Only its completion confirms success.
                        if (
                            current == PinDetailUiState.Deleted ||
                            (current is PinDetailUiState.Content && current.deletion == PinDeletionState.InProgress)
                        ) {
                            return@collect
                        }
                        _uiState.value =
                            if (pin == null) {
                                PinDetailUiState.NotFound
                            } else {
                                pin.toContent(currentLocation).copy(
                                    deletion = (current as? PinDetailUiState.Content)?.deletion ?: PinDeletionState.None,
                                )
                            }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    val current = _uiState.value
                    if (
                        current != PinDetailUiState.Deleted &&
                        !(current is PinDetailUiState.Content && current.deletion == PinDeletionState.InProgress)
                    ) {
                        _uiState.value = PinDetailUiState.Error
                    }
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
                    _uiState.value = current.pin.toContent(location).copy(deletion = current.deletion)
                }
            }
    }

    fun requestDeletion() {
        val current = _uiState.value as? PinDetailUiState.Content ?: return
        if (current.deletion == PinDeletionState.InProgress) return
        _uiState.value = current.copy(deletion = PinDeletionState.Confirmation)
    }

    fun cancelDeletion() {
        val current = _uiState.value as? PinDetailUiState.Content ?: return
        if (current.deletion == PinDeletionState.InProgress) return
        _uiState.value = current.copy(deletion = PinDeletionState.None)
    }

    fun confirmDeletion() {
        val current = _uiState.value as? PinDetailUiState.Content ?: return
        if (current.deletion != PinDeletionState.Confirmation && current.deletion != PinDeletionState.Failed) return
        _uiState.value = current.copy(deletion = PinDeletionState.InProgress)
        viewModelScope.launch {
            try {
                pinRepository.deletePin(current.pin.id)
                pinJob?.cancel()
                locationJob?.cancel()
                _uiState.value = PinDetailUiState.Deleted
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                val latest = _uiState.value as? PinDetailUiState.Content ?: current
                _uiState.value = latest.copy(deletion = PinDeletionState.Failed)
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
