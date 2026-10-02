package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class PinTest {
    @Test
    fun existingConstructionDefaultsToNoFavoriteOrPhoto() {
        val pin = Pin(PinId("pin"), GeoPoint(10.0, 20.0), SystemCategoryIds.OTHER, null, null, 100L, 200L)
        assertFalse(pin.isFavorite)
        assertNull(pin.photoReference)
        assertNull(pin.name)
        assertNull(pin.note)
    }
}
