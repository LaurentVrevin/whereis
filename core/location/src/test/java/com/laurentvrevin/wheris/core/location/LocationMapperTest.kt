package com.laurentvrevin.wheris.core.location

import android.location.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocationMapperTest {
    @Test
    fun `toUserLocation maps default location fields`() {
        val location = Location("gps")

        val userLocation = location.toUserLocation()

        assertEquals(0.0, userLocation.position.latitude, 0.0001)
        assertEquals(0.0, userLocation.position.longitude, 0.0001)
        assertNull(userLocation.accuracyMeters)
        assertNull(userLocation.altitudeMeters)
        assertEquals(0L, userLocation.timestampEpochMillis)
    }
}
