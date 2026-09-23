package com.laurentvrevin.wheris.core.model

/**
 * Portable representation of an acquired user location.
 *
 * @property position neutral geographic point (latitude and longitude)
 * @property accuracyMeters horizontal accuracy radius in meters, or null if unavailable
 * @property altitudeMeters altitude in meters, or null if unavailable
 * @property timestampEpochMillis acquisition timestamp in milliseconds since epoch
 */
data class UserLocation(
    val position: GeoPoint,
    val accuracyMeters: Float? = null,
    val altitudeMeters: Double? = null,
    val timestampEpochMillis: Long,
) {
    init {
        accuracyMeters?.let {
            require(it.isFinite() && it >= 0f) {
                "Invalid accuracyMeters: $it. Must be non-negative and finite."
            }
        }
        altitudeMeters?.let {
            require(it.isFinite()) {
                "Invalid altitudeMeters: $it. Must be finite."
            }
        }
    }
}
