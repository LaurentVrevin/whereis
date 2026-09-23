package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserLocationTest {
    @Test
    fun `valid user location initialization`() {
        val geoPoint = GeoPoint(48.8566, 2.3522)
        val userLocation =
            UserLocation(
                position = geoPoint,
                accuracyMeters = 8.5f,
                altitudeMeters = 35.0,
                timestampEpochMillis = 1000L,
            )

        assertEquals(geoPoint, userLocation.position)
        assertEquals(8.5f, userLocation.accuracyMeters)
        assertEquals(35.0, userLocation.altitudeMeters!!, 0.0001)
        assertEquals(1000L, userLocation.timestampEpochMillis)
    }

    @Test
    fun `user location with null optional fields`() {
        val geoPoint = GeoPoint(48.8566, 2.3522)
        val userLocation =
            UserLocation(
                position = geoPoint,
                timestampEpochMillis = 2000L,
            )

        assertNull(userLocation.accuracyMeters)
        assertNull(userLocation.altitudeMeters)
        assertEquals(2000L, userLocation.timestampEpochMillis)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `user location rejects negative accuracy`() {
        UserLocation(
            position = GeoPoint(48.8566, 2.3522),
            accuracyMeters = -1.0f,
            timestampEpochMillis = 1000L,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `user location rejects infinite altitude`() {
        UserLocation(
            position = GeoPoint(48.8566, 2.3522),
            altitudeMeters = Double.POSITIVE_INFINITY,
            timestampEpochMillis = 1000L,
        )
    }
}
