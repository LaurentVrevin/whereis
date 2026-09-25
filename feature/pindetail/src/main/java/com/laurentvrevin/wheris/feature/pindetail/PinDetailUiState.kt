package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.Pin

sealed interface PinDetailUiState {
    data object Loading : PinDetailUiState

    data object NotFound : PinDetailUiState

    data object Error : PinDetailUiState

    data class Content(
        val pin: Pin,
        val distanceMeters: Double? = null,
        val cardinalDirection: CardinalDirection? = null,
    ) : PinDetailUiState
}
