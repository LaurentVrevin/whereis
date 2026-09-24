package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeMapMapperTest {
    @Test
    fun `pins become one marker each`() {
        val pin =
            Pin(
                id = PinId("pin-1"),
                position = GeoPoint(48.0, 2.0),
                categoryId = CategoryId("other"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 1L,
                updatedAtEpochMillis = 1L,
            )

        val markers = listOf(pin).toMapMarkers()

        assertEquals(1, markers.size)
        assertEquals(pin.id, markers.single().pinId)
        assertEquals(pin.position, markers.single().position)
    }
}
