package com.laurentvrevin.wheris.core.location

import android.location.Location
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation

/**
 * Maps an [android.location.Location] to a portable [UserLocation].
 */
fun Location.toUserLocation(): UserLocation {
    return UserLocation(
        position =
            GeoPoint(
                latitude = latitude,
                longitude = longitude,
            ),
        accuracyMeters = if (hasAccuracy()) accuracy else null,
        altitudeMeters = if (hasAltitude()) altitude else null,
        timestampEpochMillis = time,
    )
}
