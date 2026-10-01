package com.laurentvrevin.wheris.core.model

/** Neutral keys backed by icons already available in core:ui. */
enum class CategoryIconKey(val value: String) {
    PLACE("place"),
    PHOTO_CAMERA("photo_camera"),
    PARK("park"),
    RESTAURANT("restaurant"),
    ;

    companion object {
        fun fromValue(value: String): CategoryIconKey = entries.first { it.value == value }
    }
}
