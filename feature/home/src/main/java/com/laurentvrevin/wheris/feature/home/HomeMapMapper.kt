package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.map.WherisMapMarker
import com.laurentvrevin.wheris.core.map.toWherisMapMarker
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId

internal fun List<Pin>.toMapMarkers(selectedPinId: PinId?): List<WherisMapMarker> =
    map { pin ->
        pin.toWherisMapMarker(isSelected = pin.id == selectedPinId)
    }
