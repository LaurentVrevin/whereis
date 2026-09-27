package com.laurentvrevin.wheris.core.map

import com.laurentvrevin.wheris.core.model.GeoPoint
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapCameraDecisionTest {
    @Test
    fun `should center camera on first target when no interaction occurred`() {
        val target = GeoPoint(48.0, 2.0)
        assertTrue(shouldCenterCamera(hasCenteredInitially = false, userHasInteracted = false, target = target))
    }

    @Test
    fun `should not center camera if already centered initially`() {
        val target = GeoPoint(48.0, 2.0)
        assertFalse(shouldCenterCamera(hasCenteredInitially = true, userHasInteracted = false, target = target))
    }

    @Test
    fun `should not center camera if target is null`() {
        assertFalse(shouldCenterCamera(hasCenteredInitially = false, userHasInteracted = false, target = null))
    }

    @Test
    fun `should not center camera if user has interacted`() {
        val target = GeoPoint(48.0, 2.0)
        assertFalse(shouldCenterCamera(hasCenteredInitially = false, userHasInteracted = true, target = target))
    }
}
