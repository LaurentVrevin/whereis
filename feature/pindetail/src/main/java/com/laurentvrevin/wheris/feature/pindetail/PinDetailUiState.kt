package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.Pin

sealed interface PinDetailUiState {
    data object Loading : PinDetailUiState

    data object NotFound : PinDetailUiState

    data object Error : PinDetailUiState

    data object Deleted : PinDetailUiState

    data class CleanupFailed(val inProgress: Boolean = false) : PinDetailUiState

    data class Content(
        val pin: Pin,
        val category: Category? = null,
        val distanceMeters: Double? = null,
        val cardinalDirection: CardinalDirection? = null,
        val deletion: PinDeletionState = PinDeletionState.None,
    ) : PinDetailUiState
}

enum class PinDeletionState {
    None,
    Confirmation,
    InProgress,
    Failed,
}
