package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PhotoReferenceTest {
    @Test
    fun opaqueIdentifiersRoundTripWithoutNormalization() {
        listOf("photo-42", "aB_19", "123e4567-e89b-12d3-a456-426614174000").forEach {
            assertEquals(it, PhotoReference(it).value)
        }
    }

    @Test
    fun blanksUrisPathsAndUnicodeAreNotStorageIdentifiers() {
        listOf(
            "", " ", "\n", " photo-42", "photo-42 ", "étoile", "照片", "photo.jpg",
            "..", "../photo", "photos/photo", "photos\\photo", "/storage/emulated/0/photo",
            "C:\\photo", "content://photo/42", "file:///photo", "https://example.com/photo",
        ).forEach {
            assertThrows(IllegalArgumentException::class.java) { PhotoReference(it) }
        }
    }
}
