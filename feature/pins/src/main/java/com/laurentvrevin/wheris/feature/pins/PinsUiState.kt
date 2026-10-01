package com.laurentvrevin.wheris.feature.pins

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId

data class PinItemUiState(
    val id: PinId,
    val pin: Pin,
    val category: Category? = null,
)

sealed interface PinsUiState {
    data object Loading : PinsUiState

    data object Empty : PinsUiState

    data class Content(
        val pins: List<PinItemUiState>,
    ) : PinsUiState

    data object Error : PinsUiState
}
