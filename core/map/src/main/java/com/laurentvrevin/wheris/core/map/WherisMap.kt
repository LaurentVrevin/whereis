package com.laurentvrevin.wheris.core.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.mapbox.geojson.Point
import com.mapbox.maps.AnnotatedFeature
import com.mapbox.maps.ViewAnnotationOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation

@Composable
fun WherisMap(
    markers: List<WherisMapMarker>,
    modifier: Modifier = Modifier,
    focus: GeoPoint? = null,
    onSavedPlaceClick: (PinId) -> Unit = {},
    onMapClick: () -> Unit = {},
    unavailableContent: @Composable () -> Unit,
) {
    val accessToken = stringResource(R.string.mapbox_access_token).trim()
    if (!accessToken.startsWith("pk.")) {
        unavailableContent()
        return
    }

    Log.d("WHERIS_CAMERA", "WherisMap: focus=$focus")

    val viewportState =
        rememberMapViewportState {
            setCameraOptions {
                if (focus != null) {
                    center(focus.toPoint())
                    zoom(DEFAULT_PLACE_ZOOM)
                    Log.d("WHERIS_CAMERA", "Initial camera set to focus $focus at zoom $DEFAULT_PLACE_ZOOM")
                } else {
                    zoom(DEFAULT_WORLD_ZOOM)
                    Log.d("WHERIS_CAMERA", "Initial camera set to world view zoom $DEFAULT_WORLD_ZOOM")
                }
            }
        }

    var hasCenteredInitially by remember { mutableStateOf(focus != null) }
    var userHasInteracted by remember { mutableStateOf(false) }

    LaunchedEffect(focus) {
        if (!hasCenteredInitially && focus != null && !userHasInteracted) {
            Log.d("WHERIS_CAMERA", "Triggering initial camera center on $focus at zoom $DEFAULT_PLACE_ZOOM")
            viewportState.setCameraOptions {
                center(focus.toPoint())
                zoom(DEFAULT_PLACE_ZOOM)
            }
            hasCenteredInitially = true
        }
    }

    MapboxMap(
        modifier =
            modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            userHasInteracted = true
                        }
                    }
                }
            },
        mapViewportState = viewportState,
        onMapClickListener = { _ ->
            onMapClick()
            true
        },
    ) {
        markers.forEach { marker ->
            if (marker.isCurrentLocation) {
                CircleAnnotation(point = marker.position.toPoint()) {
                    circleRadius = CURRENT_LOCATION_RADIUS
                    circleColor = Color(0xFF1976D2)
                    circleStrokeColor = Color.White
                    circleStrokeWidth = 2.0
                }
            } else {
                ViewAnnotation(
                    options =
                        ViewAnnotationOptions.Builder()
                            .annotatedFeature(AnnotatedFeature(marker.position.toPoint()))
                            .build(),
                ) {
                    WherisPinMarkerComponent(
                        categoryId = marker.categoryId,
                        isSelected = marker.isSelected,
                        onClick = {
                            marker.pinId?.let { pinId -> onSavedPlaceClick(pinId) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun WherisPinMarkerComponent(
    categoryId: CategoryId,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val visualSize = if (isSelected) 48.dp else 40.dp
    val iconSize = if (isSelected) 40.dp else 32.dp
    val borderWidth = if (isSelected) 2.dp else 1.dp // Figma contract: 2 dp for selected, 1 dp for default

    Box(
        modifier =
            Modifier
                .size(48.dp) // Guaranteed interactive hit target >= 48x48 dp
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(visualSize)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                    )
                    .border(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon(categoryId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSize),
            )
            if (isSelected) {
                Box(
                    modifier =
                        Modifier
                            .size(iconSize)
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = CircleShape,
                            ),
                )
            }
        }
    }
}

private fun GeoPoint.toPoint(): Point = Point.fromLngLat(longitude, latitude)

private const val DEFAULT_WORLD_ZOOM = 1.5
private const val DEFAULT_PLACE_ZOOM = 15.0
private const val CURRENT_LOCATION_RADIUS = 10.0
