package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.map.WherisMapMarker
import com.laurentvrevin.wheris.core.map.toWherisMapMarker
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation

internal fun List<Pin>.toMapMarkers(
    selectedPinId: PinId?,
    userLocation: UserLocation? = null,
): List<WherisMapMarker> {
    val pinMarkers =
        map { pin ->
            pin.toWherisMapMarker(isSelected = pin.id == selectedPinId)
        }
    val locationMarker =
        userLocation?.let { loc ->
            WherisMapMarker(
                pinId = null,
                position = loc.position,
                categoryId = SystemCategoryIds.OTHER,
                isCurrentLocation = true,
            )
        }
    return if (locationMarker != null) pinMarkers + locationMarker else pinMarkers
}
