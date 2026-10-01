package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CategoryTest {
    @Test
    fun systemCategoriesKeepTheirStableIdentityAndNoLocalizedMetadata() {
        SystemCategoryIds.ALL.forEach { id ->
            assertEquals(id, Category(id, true).id)
            assertThrows(IllegalArgumentException::class.java) {
                Category(id, false, "Custom", CategoryIconKey.PLACE, CategoryColorKey.ORANGE, 1L)
            }
        }
    }

    @Test
    fun customMetadataIsRequiredAndNameMustBeNormalized() {
        val id = CategoryId("custom")
        val valid = Category(id, false, "Camping", CategoryIconKey.PARK, CategoryColorKey.BLUE, 42L)
        assertEquals(42L, valid.createdAtEpochMillis)
        listOf("", " ", " Camping ").forEach { name ->
            assertThrows(IllegalArgumentException::class.java) { valid.copy(name = name) }
        }
        assertThrows(IllegalArgumentException::class.java) { valid.copy(iconKey = null) }
        assertThrows(IllegalArgumentException::class.java) { valid.copy(colorKey = null) }
        assertThrows(IllegalArgumentException::class.java) { valid.copy(createdAtEpochMillis = null) }
        assertThrows(IllegalArgumentException::class.java) { Category(id, true) }
    }

    @Test
    fun onlySupportedNeutralKeysCanBeDecoded() {
        CategoryIconKey.entries.forEach { assertEquals(it, CategoryIconKey.fromValue(it.value)) }
        CategoryColorKey.entries.forEach { assertEquals(it, CategoryColorKey.fromValue(it.value)) }
        assertThrows(NoSuchElementException::class.java) { CategoryIconKey.fromValue("unknown") }
        assertThrows(NoSuchElementException::class.java) { CategoryColorKey.fromValue("#FFFFFF") }
    }
}
