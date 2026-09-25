package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMapMapperTest {
    @Test
    fun `pins become markers with selection state`() {
        val pin1 =
            Pin(
                id = PinId("pin-1"),
                position = GeoPoint(48.0, 2.0),
                categoryId = CategoryId("other"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 1L,
                updatedAtEpochMillis = 1L,
            )
        val pin2 =
            Pin(
                id = PinId("pin-2"),
                position = GeoPoint(49.0, 3.0),
                categoryId = CategoryId("car"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 2L,
                updatedAtEpochMillis = 2L,
            )

        val markers = listOf(pin1, pin2).toMapMarkers(selectedPinId = pin1.id)

        assertEquals(2, markers.size)
        assertTrue(markers[0].isSelected)
        assertFalse(markers[1].isSelected)
        assertEquals(pin1.id, markers[0].pinId)
        assertEquals(pin2.id, markers[1].pinId)
    }
}
