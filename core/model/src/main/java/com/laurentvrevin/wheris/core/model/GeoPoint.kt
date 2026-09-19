package com.laurentvrevin.wheris.core.model

/**
 * Portable representation of a geographic point.
 *
 * @property latitude in degrees, range [-90, 90]
 * @property longitude in degrees, range [-180, 180]
 */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "Invalid latitude: $latitude. Must be in range [-90, 90]."
        }
        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "Invalid longitude: $longitude. Must be in range [-180, 180]."
        }
    }
}
