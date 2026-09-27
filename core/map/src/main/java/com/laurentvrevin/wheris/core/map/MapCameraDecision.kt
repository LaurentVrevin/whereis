package com.laurentvrevin.wheris.core.map

import com.laurentvrevin.wheris.core.model.GeoPoint

fun shouldCenterCamera(
    hasCenteredInitially: Boolean,
    userHasInteracted: Boolean,
    target: GeoPoint?,
): Boolean = !hasCenteredInitially && target != null && !userHasInteracted
