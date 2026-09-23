package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.UserLocation

/**
 * UI State for current location acquisition in Add Pin flow (A3 validation).
 */
sealed interface AddPinUiState {
    data object PermissionRequired : AddPinUiState

    data class PermissionDenied(val isPermanentlyDenied: Boolean = false) : AddPinUiState

    data object Searching : AddPinUiState

    data class Success(
        val location: UserLocation,
        val isApproximate: Boolean,
    ) : AddPinUiState

    data object ServicesDisabled : AddPinUiState

    data object Timeout : AddPinUiState

    data class Error(val message: String? = null) : AddPinUiState
}
