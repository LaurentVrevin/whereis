package com.laurentvrevin.wheris.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class IdTest {
    @Test
    fun `valid PinId should be accepted`() {
        val id = PinId("pin_123")
        assertEquals("pin_123", id.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty PinId should throw exception`() {
        PinId("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank PinId should throw exception`() {
        PinId("   ")
    }

    @Test
    fun `valid CategoryId should be accepted`() {
        val id = CategoryId("cat_123")
        assertEquals("cat_123", id.value)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty CategoryId should throw exception`() {
        CategoryId("")
    }
}
