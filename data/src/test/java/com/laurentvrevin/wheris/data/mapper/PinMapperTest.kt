package com.laurentvrevin.wheris.data.mapper

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PinMapperTest {
    private val original = Pin(PinId("pin"), GeoPoint(10.0, 20.0), SystemCategoryIds.OTHER, 7f, 35.0, 1000L, 2000L)

    @Test
    fun allOptionalFieldCombinationsRoundTripWithoutLoss() {
        listOf(false, true).forEach { favorite ->
            listOf(null, PhotoReference("photo-42")).forEach { photo ->
                listOf(null, "Café 東京 🌲").forEach { name ->
                    listOf(null, "  Note\n\n第二行  ").forEach { note ->
                        val pin = original.copy(name = name, note = note, isFavorite = favorite, photoReference = photo)
                        val entity = pin.toEntity()
                        assertEquals(favorite, entity.isFavorite)
                        assertEquals(photo?.value, entity.photoReference)
                        assertEquals(pin, entity.toDomain())
                    }
                }
            }
        }
    }

    @Test
    fun invalidPersistedPhotoReferencesFailExplicitly() {
        listOf("", " ", "../photo", "content://photo/42").forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) {
                original.toEntity().copy(photoReference = invalid).toDomain()
            }
        }
    }
}
