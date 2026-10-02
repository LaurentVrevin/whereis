package com.laurentvrevin.wheris.core.database.dao

enum class PinMutationStatus {
    SUCCESS,
    PIN_NOT_FOUND,
    CATEGORY_NOT_FOUND,
    PHOTO_ALREADY_ATTACHED,
    CONFLICT,
}
