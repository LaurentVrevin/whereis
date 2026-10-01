package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.UserLocation

sealed interface AddPinUiState {
    data object PermissionRequired : AddPinUiState

    data class PermissionDenied(
        val isPermanentlyDenied: Boolean = false,
    ) : AddPinUiState

    data object Searching : AddPinUiState

    data class PositionFound(
        val location: UserLocation,
        val isApproximate: Boolean,
    ) : AddPinUiState

    data object ServicesDisabled : AddPinUiState

    data object Timeout : AddPinUiState

    data object TechnicalError : AddPinUiState

    data class CategorySelection(
        val location: UserLocation,
        val categories: List<Category> = emptyList(),
        val selectedCategoryId: CategoryId? = null,
        val isLoadingCategories: Boolean = true,
        val categoryLoadFailed: Boolean = false,
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
    ) : AddPinUiState

    data class CategoryCreation(
        val selection: CategorySelection,
        val name: String = "",
        val iconKey: CategoryIconKey = CategoryIconKey.PLACE,
        val colorKey: CategoryColorKey = CategoryColorKey.ORANGE,
        val isCreating: Boolean = false,
        val creationFailed: Boolean = false,
    ) : AddPinUiState {
        val canCreate: Boolean get() = name.isNotBlank() && !isCreating
    }

    data class Saved(
        val pin: Pin,
    ) : AddPinUiState
}
