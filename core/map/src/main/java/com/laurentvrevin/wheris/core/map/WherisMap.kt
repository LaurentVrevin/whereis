package com.laurentvrevin.wheris.core.map

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
import com.laurentvrevin.wheris.core.ui.category.categoryColor
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.mapbox.geojson.Point
import com.mapbox.maps.AnnotatedFeature
import com.mapbox.maps.ViewAnnotationOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.rememberMapState

@Composable
fun WherisMap(
    markers: List<WherisMapMarker>,
    modifier: Modifier = Modifier,
    focus: GeoPoint? = null,
    onSavedPlaceClick: (PinId) -> Unit = {},
    onMapClick: () -> Unit = {},
    unavailableContent: @Composable (onRetry: (() -> Unit)?) -> Unit,
) {
    val accessToken = stringResource(R.string.mapbox_access_token).trim()
    if (!accessToken.startsWith("pk.")) {
        unavailableContent(null)
        return
    }

    val mapState = rememberMapState()
    var loadFailed by remember { mutableStateOf(false) }
    var hasLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapState) {
        mapState.mapLoadedEvents.collect { hasLoaded = true }
    }

    LaunchedEffect(mapState) {
        mapState.mapLoadingErrorEvents.collect {
            // Provider errors may contain URLs or coordinates: never log their payload.
            if (shouldShowMapFallback(hasLoaded, it.type)) {
                loadFailed = true
            }
        }
    }

    val viewportState =
        rememberMapViewportState {
            setCameraOptions {
                if (focus != null) {
                    center(focus.toPoint())
                    zoom(DEFAULT_PLACE_ZOOM)
                } else {
                    zoom(DEFAULT_WORLD_ZOOM)
                }
            }
        }

    var hasCenteredInitially by remember { mutableStateOf(focus != null) }
    var userHasInteracted by remember { mutableStateOf(false) }

    LaunchedEffect(focus) {
        if (focus != null && shouldCenterCamera(hasCenteredInitially, userHasInteracted, focus)) {
            viewportState.setCameraOptions {
                center(focus.toPoint())
                zoom(DEFAULT_PLACE_ZOOM)
            }
            hasCenteredInitially = true
        }
    }

    if (loadFailed) {
        unavailableContent {
            hasLoaded = false
            loadFailed = false
        }
        return
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
        mapState = mapState,
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
                        category = marker.category,
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
    category: com.laurentvrevin.wheris.core.model.Category?,
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
                        color = categoryColor(category),
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon(categoryId, category),
                contentDescription = null,
                tint = if (category?.isSystem == false) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
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
