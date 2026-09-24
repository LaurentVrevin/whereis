package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemCategoryIdsTest {
    @Test
    fun `system category ids are stable and ordered`() {
        assertEquals(
            listOf(
                "car",
                "tent",
                "bivouac",
                "restaurant",
                "photo_spot",
                "viewpoint",
                "bike",
                "parking",
                "beach",
                "fishing",
                "hiking",
                "meeting",
                "other",
            ),
            SystemCategoryIds.ALL.map { it.value },
        )
    }
}
