package com.laurentvrevin.wheris.core.map

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId

data class WherisMapMarker(
    val pinId: PinId?,
    val position: GeoPoint,
    val isCurrentLocation: Boolean = false,
)

fun Pin.toWherisMapMarker(): WherisMapMarker =
    WherisMapMarker(
        pinId = id,
        position = position,
    )
