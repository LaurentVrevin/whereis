package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinUpdateResult

sealed interface EditPhotoState {
    data object Unchanged : EditPhotoState

    data object Removed : EditPhotoState

    data class Replacement(val draft: PhotoDraftReference, val originalRemoved: Boolean = false) : EditPhotoState
}

sealed interface EditPinUiState {
    data object Loading : EditPinUiState

    data object NotFound : EditPinUiState

    data object LoadFailed : EditPinUiState

    data object Saved : EditPinUiState

    data class Content(
        val pinId: PinId,
        val name: String,
        val note: String,
        val categories: List<Category>,
        val selectedCategoryId: CategoryId,
        val isFavorite: Boolean,
        val originalPhoto: PhotoReference?,
        val photo: EditPhotoState = EditPhotoState.Unchanged,
        val isSaving: Boolean = false,
        val isAcquiringPhoto: Boolean = false,
        val photoFailed: Boolean = false,
        val categoriesFailed: Boolean = false,
        val saveError: PinUpdateResult? = null,
    ) : EditPinUiState {
        val hasPhoto: Boolean get() = photo is EditPhotoState.Replacement || (photo == EditPhotoState.Unchanged && originalPhoto != null)
        val canSave: Boolean get() =
            !isSaving && !isAcquiringPhoto && !categoriesFailed &&
                saveError != PinUpdateResult.PIN_NOT_FOUND && categories.any { it.id == selectedCategoryId }
    }
}
