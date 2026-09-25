package com.laurentvrevin.wheris.core.map

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WherisMapMarkerTest {
    @Test
    fun `pin mapping preserves id and position`() {
        val pin =
            Pin(
                id = PinId("pin-1"),
                position =
                    GeoPoint(
                        latitude = 48.0,
                        longitude = 2.0,
                    ),
                categoryId = CategoryId("other"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 1L,
                updatedAtEpochMillis = 1L,
            )

        val marker = pin.toWherisMapMarker()

        assertEquals(pin.id, marker.pinId)
        assertEquals(pin.position, marker.position)
        assertEquals(pin.categoryId, marker.categoryId)
        assertFalse(marker.isCurrentLocation)
        assertFalse(marker.isSelected)
    }

    @Test
    fun `pin mapping preserves id position category and selection`() {
        val pin =
            Pin(
                id = PinId("pin-1"),
                position = GeoPoint(48.0, 2.0),
                categoryId = CategoryId("car"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 1L,
                updatedAtEpochMillis = 1L,
            )

        val marker = pin.toWherisMapMarker(isSelected = true)

        assertEquals(pin.id, marker.pinId)
        assertEquals(pin.position, marker.position)
        assertEquals(pin.categoryId, marker.categoryId)
        assertFalse(marker.isCurrentLocation)
        assertTrue(marker.isSelected)
    }
}
