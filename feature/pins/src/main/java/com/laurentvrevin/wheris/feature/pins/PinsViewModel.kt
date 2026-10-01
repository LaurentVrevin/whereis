package com.laurentvrevin.wheris.feature.pins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PinsViewModel(
    pinRepository: PinRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {
    val uiState: StateFlow<PinsUiState> =
        combine(pinRepository.observePins(), categoryRepository.observeCategories()) { pins, categories ->
            if (pins.isEmpty()) {
                PinsUiState.Empty
            } else {
                PinsUiState.Content(
                    pins =
                        pins.map { pin ->
                            PinItemUiState(
                                id = pin.id,
                                pin = pin,
                                category = categories.firstOrNull { it.id == pin.categoryId },
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
