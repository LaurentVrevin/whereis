package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GeoPointTest {
    @Test
    fun `valid coordinates should be accepted`() {
        val point = GeoPoint(latitude = 48.8566, longitude = 2.3522)
        assertEquals(48.8566, point.latitude, 0.0)
        assertEquals(2.3522, point.longitude, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `latitude below -90 should throw exception`() {
        GeoPoint(latitude = -90.1, longitude = 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `latitude above 90 should throw exception`() {
        GeoPoint(latitude = 90.1, longitude = 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `longitude below -180 should throw exception`() {
        GeoPoint(latitude = 0.0, longitude = -180.1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `longitude above 180 should throw exception`() {
        GeoPoint(latitude = 0.0, longitude = 180.1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `NaN coordinates should throw exception`() {
        GeoPoint(latitude = Double.NaN, longitude = 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `infinite coordinates should throw exception`() {
        GeoPoint(latitude = Double.POSITIVE_INFINITY, longitude = 0.0)
    }
}
