package com.laurentvrevin.wheris.core.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the Haversine distance in meters between two [GeoPoint]s.
 */
fun GeoPoint.distanceTo(other: GeoPoint): Double {
    val lat1Rad = Math.toRadians(latitude)
    val lat2Rad = Math.toRadians(other.latitude)
    val deltaLatRad = Math.toRadians(other.latitude - latitude)
    val deltaLonRad = Math.toRadians(other.longitude - longitude)

    val a =
        sin(deltaLatRad / 2.0).pow(2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2.0).pow(2)
    val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))

    return EARTH_RADIUS_METERS * c
}

/**
 * Calculates the initial bearing in degrees in range [0.0, 360.0) from this [GeoPoint] to [other].
 */
fun GeoPoint.bearingTo(other: GeoPoint): Double {
    val lat1Rad = Math.toRadians(latitude)
    val lat2Rad = Math.toRadians(other.latitude)
    val deltaLonRad = Math.toRadians(other.longitude - longitude)

    val y = sin(deltaLonRad) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)

    val bearingRad = atan2(y, x)
    val bearingDeg = Math.toDegrees(bearingRad)
    return (bearingDeg % 360.0 + 360.0) % 360.0
}

/**
 * 8-point cardinal directions.
 */
enum class CardinalDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    ;

    companion object {
        /**
         * Derives the cardinal direction from a bearing in degrees.
         */
        fun fromBearing(bearingDegrees: Double): CardinalDirection {
            val normalized = (bearingDegrees % 360.0 + 360.0) % 360.0
            return when {
                normalized >= 337.5 || normalized < 22.5 -> NORTH
                normalized < 67.5 -> NORTH_EAST
                normalized < 112.5 -> EAST
                normalized < 157.5 -> SOUTH_EAST
                normalized < 202.5 -> SOUTH
                normalized < 247.5 -> SOUTH_WEST
                normalized < 292.5 -> WEST
                else -> NORTH_WEST
            }
        }
    }
}

private const val EARTH_RADIUS_METERS = 6_371_000.0
