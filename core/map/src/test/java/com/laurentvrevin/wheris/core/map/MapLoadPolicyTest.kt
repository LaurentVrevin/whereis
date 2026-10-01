package com.laurentvrevin.wheris.core.map

import com.mapbox.maps.MapLoadingErrorType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapLoadPolicyTest {
    @Test
    fun `a failed first load offers the fallback regardless of resource`() {
        MapLoadingErrorType.values().forEach { type ->
            assertTrue(shouldShowMapFallback(hasLoaded = false, errorType = type))
        }
    }

    @Test
    fun `a style failure makes even a previously loaded map unavailable`() {
        assertTrue(shouldShowMapFallback(hasLoaded = true, errorType = MapLoadingErrorType.STYLE))
    }

    @Test
    fun `resource failures preserve an already loaded map for offline use`() {
        listOf(
            MapLoadingErrorType.TILE,
            MapLoadingErrorType.SOURCE,
            MapLoadingErrorType.SPRITE,
            MapLoadingErrorType.GLYPHS,
        ).forEach { type ->
            assertFalse(shouldShowMapFallback(hasLoaded = true, errorType = type))
        }
    }
}
