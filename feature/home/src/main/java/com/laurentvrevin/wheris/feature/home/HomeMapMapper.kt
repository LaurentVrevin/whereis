package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.map.WherisMapMarker
import com.laurentvrevin.wheris.core.map.toWherisMapMarker
import com.laurentvrevin.wheris.core.model.Pin

internal fun List<Pin>.toMapMarkers(): List<WherisMapMarker> = map { it.toWherisMapMarker() }
