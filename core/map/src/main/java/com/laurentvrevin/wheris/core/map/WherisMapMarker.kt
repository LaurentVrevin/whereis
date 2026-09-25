package com.laurentvrevin.wheris.core.map

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds

data class WherisMapMarker(
    val pinId: PinId?,
    val position: GeoPoint,
    val categoryId: CategoryId = SystemCategoryIds.OTHER,
    val isCurrentLocation: Boolean = false,
    val isSelected: Boolean = false,
)

fun Pin.toWherisMapMarker(isSelected: Boolean = false): WherisMapMarker =
    WherisMapMarker(
        pinId = id,
        position = position,
        categoryId = categoryId,
        isSelected = isSelected,
    )
