package com.laurentvrevin.wheris.feature.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationPermissionStateTest {
    @Test
    fun `false rationale before any request is not a permanent denial`() {
        assertEquals(LocationPermissionState.NotRequested, locationPermissionState(false, false, false, false))
    }

    @Test
    fun `existing permission does not require a new request`() {
        assertEquals(LocationPermissionState.Precise, locationPermissionState(true, true, false, false))
        assertEquals(LocationPermissionState.Approximate, locationPermissionState(false, true, false, false))
    }

    @Test
    fun `fine permission result is accepted`() {
        assertEquals(LocationPermissionState.Precise, locationPermissionState(true, true, true, false))
    }

    @Test
    fun `coarse only permission result is accepted`() {
        assertEquals(LocationPermissionState.Approximate, locationPermissionState(false, true, true, false))
    }

    @Test
    fun `denial with rationale is requestable again`() {
        assertEquals(LocationPermissionState.Denied, locationPermissionState(false, false, true, true))
    }

    @Test
    fun `negative result without rationale requires settings`() {
        assertEquals(LocationPermissionState.SettingsRequired, locationPermissionState(false, false, true, false))
    }

    @Test
    fun `settings return with a grant replaces denied state`() {
        assertEquals(LocationPermissionState.SettingsRequired, locationPermissionState(false, false, true, false))
        assertEquals(LocationPermissionState.Approximate, locationPermissionState(false, true, true, false))
        assertEquals(LocationPermissionState.Precise, locationPermissionState(true, true, true, false))
    }
}
