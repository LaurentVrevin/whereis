package com.laurentvrevin.wheris.feature.pins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.domain.PinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PinsViewModel(
    pinRepository: PinRepository,
) : ViewModel() {
    val uiState: StateFlow<PinsUiState> =
        pinRepository
            .observePins()
            .map { pins ->
                if (pins.isEmpty()) {
                    PinsUiState.Empty
                } else {
                    PinsUiState.Content(
                        pins =
                            pins.map { pin ->
                                PinItemUiState(
                                    id = pin.id,
                                    pin = pin,
                                )
                            },
                    )
                }
            }
            .catch { emit(PinsUiState.Error) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PinsUiState.Loading,
            )
}
