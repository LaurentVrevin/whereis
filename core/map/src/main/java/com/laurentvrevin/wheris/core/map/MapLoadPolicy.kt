package com.laurentvrevin.wheris.core.map

import com.mapbox.maps.MapLoadingErrorType

/** Keep an already rendered map usable when only a tile or another resource fails. */
internal fun shouldShowMapFallback(
    hasLoaded: Boolean,
    errorType: MapLoadingErrorType,
): Boolean = !hasLoaded || errorType == MapLoadingErrorType.STYLE
