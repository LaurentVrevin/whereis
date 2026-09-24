package com.laurentvrevin.wheris.core.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation

@Composable
fun WherisMap(
    markers: List<WherisMapMarker>,
    modifier: Modifier = Modifier,
    focus: GeoPoint? = null,
    unavailableContent: @Composable () -> Unit,
) {
    val accessToken = stringResource(R.string.mapbox_access_token).trim()
    if (!accessToken.startsWith("pk.")) {
        unavailableContent()
        return
    }

    val initialTarget = focus ?: markers.firstOrNull()?.position
    val viewportState =
        rememberMapViewportState {
            setCameraOptions {
                if (initialTarget != null) {
                    center(initialTarget.toPoint())
                    zoom(DEFAULT_PLACE_ZOOM)
                } else {
                    zoom(DEFAULT_WORLD_ZOOM)
                }
            }
        }

    LaunchedEffect(focus) {
        focus?.let { point ->
            viewportState.setCameraOptions {
                center(point.toPoint())
                zoom(DEFAULT_PLACE_ZOOM)
            }
        }
    }

    MapboxMap(
        modifier = modifier,
        mapViewportState = viewportState,
    ) {
        markers.forEach { marker ->
            CircleAnnotation(point = marker.position.toPoint()) {
                circleRadius = if (marker.isCurrentLocation) CURRENT_LOCATION_RADIUS else SAVED_PIN_RADIUS
                circleColor = if (marker.isCurrentLocation) Color(0xFF1976D2) else Color(0xFFE66A1F)
                circleStrokeColor = Color.White
                circleStrokeWidth = 2.0
            }
        }
    }
}

private fun GeoPoint.toPoint(): Point = Point.fromLngLat(longitude, latitude)

private const val DEFAULT_WORLD_ZOOM = 1.5
private const val DEFAULT_PLACE_ZOOM = 15.0
private const val SAVED_PIN_RADIUS = 8.0
private const val CURRENT_LOCATION_RADIUS = 10.0
