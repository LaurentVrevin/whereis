package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GeoLocationUtilsTest {
    @Test
    fun `distance to same point is zero`() {
        val point = GeoPoint(10.0, 20.0)

        assertEquals(
            0.0,
            point.distanceTo(point),
            0.001,
        )
    }

    @Test
    fun `one degree of longitude at equator is about 111 kilometers`() {
        val start = GeoPoint(0.0, 0.0)
        val east = GeoPoint(0.0, 1.0)

        assertEquals(
            111_194.9,
            start.distanceTo(east),
            2.0,
        )
    }

    @Test
    fun `bearing due north is zero degrees`() {
        assertEquals(
            0.0,
            GeoPoint(0.0, 0.0).bearingTo(
                GeoPoint(10.0, 0.0),
            ),
            0.001,
        )
    }

    @Test
    fun `bearing due east is 90 degrees`() {
        assertEquals(
            90.0,
            GeoPoint(0.0, 0.0).bearingTo(
                GeoPoint(0.0, 10.0),
            ),
            0.001,
        )
    }

    @Test
    fun `bearing due south is 180 degrees`() {
        assertEquals(
            180.0,
            GeoPoint(0.0, 0.0).bearingTo(
                GeoPoint(-10.0, 0.0),
            ),
            0.001,
        )
    }

    @Test
    fun `bearing due west is normalized to 270 degrees`() {
        assertEquals(
            270.0,
            GeoPoint(0.0, 0.0).bearingTo(
                GeoPoint(0.0, -10.0),
            ),
            0.001,
        )
    }

    @Test
    fun `cardinal direction normalizes negative and over 360 bearings`() {
        assertEquals(
            CardinalDirection.NORTH,
            CardinalDirection.fromBearing(-10.0),
        )
        assertEquals(
            CardinalDirection.NORTH,
            CardinalDirection.fromBearing(370.0),
        )
        assertEquals(
            CardinalDirection.WEST,
            CardinalDirection.fromBearing(630.0),
        )
    }

    @Test
    fun `cardinal direction uses documented 8 point boundaries`() {
        assertEquals(
            CardinalDirection.NORTH,
            CardinalDirection.fromBearing(0.0),
        )
        assertEquals(
            CardinalDirection.NORTH,
            CardinalDirection.fromBearing(22.499),
        )
        assertEquals(
            CardinalDirection.NORTH_EAST,
            CardinalDirection.fromBearing(22.5),
        )
        assertEquals(
            CardinalDirection.NORTH_EAST,
            CardinalDirection.fromBearing(67.499),
        )
        assertEquals(
            CardinalDirection.EAST,
            CardinalDirection.fromBearing(67.5),
        )
        assertEquals(
            CardinalDirection.SOUTH_EAST,
            CardinalDirection.fromBearing(135.0),
        )
        assertEquals(
            CardinalDirection.SOUTH,
            CardinalDirection.fromBearing(180.0),
        )
        assertEquals(
            CardinalDirection.SOUTH_WEST,
            CardinalDirection.fromBearing(225.0),
        )
        assertEquals(
            CardinalDirection.WEST,
            CardinalDirection.fromBearing(270.0),
        )
        assertEquals(
            CardinalDirection.NORTH_WEST,
            CardinalDirection.fromBearing(315.0),
        )
        assertEquals(
            CardinalDirection.NORTH,
            CardinalDirection.fromBearing(337.5),
        )
    }
}
