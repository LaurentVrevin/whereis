package com.laurentvrevin.wheris.domain

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId

/** Only editable fields: geographic and creation metadata cannot enter a mutation. */
data class PinUpdate(
    val pinId: PinId,
    val categoryId: CategoryId,
    val name: String?,
    val note: String?,
    val isFavorite: Boolean,
    val photoChange: PinPhotoChange,
)

sealed interface PinPhotoChange {
    data object Keep : PinPhotoChange

    data object Remove : PinPhotoChange

    data class Replace(val photo: PhotoReference) : PinPhotoChange
}

enum class PinUpdateResult {
    SUCCESS,

    /** The DB is committed. Pending file cleanup will be retried by reconciliation. */
    SUCCESS_WITH_CLEANUP_PENDING,
    PIN_NOT_FOUND,
    CATEGORY_NOT_FOUND,
    PHOTO_ALREADY_ATTACHED,
    TECHNICAL_FAILURE,
}
