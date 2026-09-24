package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.map.WherisMap
import com.laurentvrevin.wheris.core.map.WherisMapMarker
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation

@Composable
fun AddPinScreen(
    uiState: AddPinUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onRetryLocation: () -> Unit,
    onConfirmPosition: () -> Unit,
    onSelectCategory: (CategoryId) -> Unit,
    onSave: () -> Unit,
    onBackToPosition: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (uiState) {
            is AddPinUiState.PermissionRequired ->
                CenteredContent {
                    PermissionRequiredContent(onRequestPermission)
                }

            is AddPinUiState.PermissionDenied ->
                CenteredContent {
                    PermissionDeniedContent(
                        state = uiState,
                        onOpenSettings = onOpenSettings,
                        onRetry = onRetryLocation,
                    )
                }

            is AddPinUiState.Searching ->
                CenteredContent {
                    SearchingContent()
                }

            is AddPinUiState.PositionFound ->
                PositionFoundContent(
                    state = uiState,
                    onConfirmPosition = onConfirmPosition,
                    onRetry = onRetryLocation,
                )

            is AddPinUiState.ServicesDisabled ->
                CenteredContent {
                    ServicesDisabledContent(
                        onOpenLocationSettings = onOpenLocationSettings,
                        onRetry = onRetryLocation,
                    )
                }

            is AddPinUiState.Timeout ->
                CenteredContent {
                    TimeoutContent(onRetryLocation)
                }

            is AddPinUiState.TechnicalError ->
                CenteredContent {
                    TechnicalErrorContent(onRetryLocation)
                }

            is AddPinUiState.CategorySelection ->
                CategorySelectionContent(
                    state = uiState,
                    onSelectCategory = onSelectCategory,
                    onSave = onSave,
                    onBack = onBackToPosition,
                )

            is AddPinUiState.Saved ->
                CenteredContent {
                    SavedContent(onFinished)
                }
        }
    }
}

@Composable
private fun CenteredContent(content: @Composable () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(WherisSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.addpin_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = WherisSpacing.xl),
        )
        content()
    }
}

@Composable
private fun PermissionRequiredContent(onRequestPermission: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(WherisSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(WherisSpacing.md))
            Text(
                text = stringResource(R.string.addpin_permission_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(WherisSpacing.sm))
            Text(
                text = stringResource(R.string.addpin_permission_rationale),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(WherisSpacing.lg))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.addpin_btn_grant_permission))
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(
    state: AddPinUiState.PermissionDenied,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(48.dp),
    )
    Spacer(Modifier.height(WherisSpacing.md))
    Text(
        text = stringResource(R.string.addpin_permission_denied_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(Modifier.height(WherisSpacing.sm))
    Text(
        text = stringResource(R.string.addpin_permission_denied_msg),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(WherisSpacing.lg))
    if (state.isPermanentlyDenied) {
        Button(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.addpin_btn_open_settings))
        }
    } else {
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.addpin_btn_retry))
        }
    }
}

@Composable
private fun SearchingContent() {
    CircularProgressIndicator()
    Spacer(Modifier.height(WherisSpacing.lg))
    Text(
        text = stringResource(R.string.addpin_searching_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(Modifier.height(WherisSpacing.sm))
    Text(
        text = stringResource(R.string.addpin_searching_subtitle),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun PositionFoundContent(
    state: AddPinUiState.PositionFound,
    onConfirmPosition: () -> Unit,
    onRetry: () -> Unit,
) {
    val location = state.location
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            WherisMap(
                markers =
                    listOf(
                        WherisMapMarker(
                            pinId = null,
                            position = location.position,
                            isCurrentLocation = true,
                        ),
                    ),
                focus = location.position,
                modifier = Modifier.fillMaxSize(),
                unavailableContent = {
                    MapUnavailableLocationContent(
                        latitude = location.position.latitude,
                        longitude = location.position.longitude,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(WherisSpacing.lg),
        ) {
            Column(modifier = Modifier.padding(WherisSpacing.lg)) {
                Text(
                    text = stringResource(R.string.addpin_success_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                if (state.isApproximate) {
                    Text(
                        text = stringResource(R.string.addpin_approximate_badge),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = WherisSpacing.xs),
                    )
                }
                Text(
                    text =
                        location.accuracyMeters?.let {
                            stringResource(R.string.addpin_accuracy_format, it)
                        } ?: stringResource(R.string.addpin_accuracy_unknown),
                    modifier = Modifier.padding(top = WherisSpacing.sm),
                )
                location.altitudeMeters?.let {
                    Text(stringResource(R.string.addpin_altitude_format, it))
                }
                Spacer(Modifier.height(WherisSpacing.lg))
                Button(
                    onClick = onConfirmPosition,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.addpin_btn_confirm_position))
                }
                OutlinedButton(
                    onClick = onRetry,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = WherisSpacing.sm),
                ) {
                    Text(stringResource(R.string.addpin_btn_retry))
                }
            }
        }
    }
}

@Composable
private fun MapUnavailableLocationContent(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(WherisSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = stringResource(R.string.addpin_map_unavailable),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = WherisSpacing.sm),
        )
        Text(
            text = stringResource(R.string.addpin_coordinates_format, latitude, longitude),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = WherisSpacing.sm),
        )
    }
}

@Composable
private fun ServicesDisabledContent(
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
) {
    Icon(
        imageVector = Icons.Default.LocationOff,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(48.dp),
    )
    Spacer(Modifier.height(WherisSpacing.md))
    Text(
        text = stringResource(R.string.addpin_services_disabled_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(Modifier.height(WherisSpacing.sm))
    Text(
        text = stringResource(R.string.addpin_services_disabled_msg),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(WherisSpacing.lg))
    Button(
        onClick = onOpenLocationSettings,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.addpin_btn_open_location_settings))
    }
    OutlinedButton(
        onClick = onRetry,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = WherisSpacing.sm),
    ) {
        Text(stringResource(R.string.addpin_btn_retry))
    }
}

@Composable
private fun TimeoutContent(onRetry: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(48.dp),
    )
    Spacer(Modifier.height(WherisSpacing.md))
    Text(
        text = stringResource(R.string.addpin_timeout_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(Modifier.height(WherisSpacing.sm))
    Text(
        text = stringResource(R.string.addpin_timeout_msg),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(WherisSpacing.lg))
    Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.addpin_btn_retry))
    }
}

@Composable
private fun TechnicalErrorContent(onRetry: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Error,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(48.dp),
    )
    Spacer(Modifier.height(WherisSpacing.md))
    Text(
        text = stringResource(R.string.addpin_error_title),
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(Modifier.height(WherisSpacing.sm))
    Text(
        text = stringResource(R.string.addpin_error_msg),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(WherisSpacing.lg))
    Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.addpin_btn_retry))
    }
}

@Composable
private fun CategorySelectionContent(
    state: AddPinUiState.CategorySelection,
    onSelectCategory: (CategoryId) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(WherisSpacing.lg),
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.addpin_back_to_position))
        }
        Text(
            text = stringResource(R.string.addpin_category_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = WherisSpacing.md),
        )

        when {
            state.isLoadingCategories -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.categoryLoadFailed -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.addpin_category_load_error),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                ) {
                    items(
                        items = state.categories,
                        key = { it.id.value },
                    ) { category ->
                        CategoryRow(
                            category = category,
                            selected = state.selectedCategoryId == category.id,
                            enabled = !state.isSaving,
                            onClick = { onSelectCategory(category.id) },
                        )
                    }
                }
            }
        }

        if (state.saveFailed) {
            Text(
                text = stringResource(R.string.addpin_save_error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = WherisSpacing.sm),
            )
        }

        Button(
            onClick = onSave,
            enabled =
                state.selectedCategoryId != null &&
                    !state.isSaving &&
                    !state.isLoadingCategories &&
                    !state.categoryLoadFailed,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.addpin_btn_save))
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick),
        border =
            if (selected) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(WherisSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = categoryIcon(category.id),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = categoryLabel(category.id),
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = WherisSpacing.md),
                style = MaterialTheme.typography.bodyLarge,
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.addpin_category_selected),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun categoryLabel(categoryId: CategoryId): String =
    when (categoryId) {
        SystemCategoryIds.CAR -> stringResource(R.string.category_car)
        SystemCategoryIds.TENT -> stringResource(R.string.category_tent)
        SystemCategoryIds.BIVOUAC -> stringResource(R.string.category_bivouac)
        SystemCategoryIds.RESTAURANT -> stringResource(R.string.category_restaurant)
        SystemCategoryIds.PHOTO_SPOT -> stringResource(R.string.category_photo_spot)
        SystemCategoryIds.VIEWPOINT -> stringResource(R.string.category_viewpoint)
        SystemCategoryIds.BIKE -> stringResource(R.string.category_bike)
        SystemCategoryIds.PARKING -> stringResource(R.string.category_parking)
        SystemCategoryIds.BEACH -> stringResource(R.string.category_beach)
        SystemCategoryIds.FISHING -> stringResource(R.string.category_fishing)
        SystemCategoryIds.HIKING -> stringResource(R.string.category_hiking)
        SystemCategoryIds.MEETING -> stringResource(R.string.category_meeting)
        SystemCategoryIds.OTHER -> stringResource(R.string.category_other)
        else -> categoryId.value
    }

private fun categoryIcon(categoryId: CategoryId): ImageVector =
    when (categoryId) {
        SystemCategoryIds.CAR -> Icons.Default.DirectionsCar
        SystemCategoryIds.TENT -> Icons.Default.Home
        SystemCategoryIds.BIVOUAC -> Icons.Default.Park
        SystemCategoryIds.RESTAURANT -> Icons.Default.Restaurant
        SystemCategoryIds.PHOTO_SPOT -> Icons.Default.PhotoCamera
        SystemCategoryIds.VIEWPOINT -> Icons.Default.Visibility
        SystemCategoryIds.BIKE -> Icons.Default.DirectionsBike
        SystemCategoryIds.PARKING -> Icons.Default.LocalParking
        SystemCategoryIds.BEACH -> Icons.Default.BeachAccess
        SystemCategoryIds.FISHING -> Icons.Default.Place
        SystemCategoryIds.HIKING -> Icons.Default.DirectionsWalk
        SystemCategoryIds.MEETING -> Icons.Default.Event
        SystemCategoryIds.OTHER -> Icons.Default.MoreHoriz
        else -> Icons.Default.Landscape
    }

@Composable
private fun SavedContent(onFinished: () -> Unit) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(64.dp),
    )
    Spacer(Modifier.height(WherisSpacing.lg))
    Text(
        text = stringResource(R.string.addpin_saved_title),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(WherisSpacing.xl))
    Button(
        onClick = onFinished,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.addpin_view_on_map))
    }
    OutlinedButton(
        onClick = onFinished,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = WherisSpacing.sm),
    ) {
        Text(stringResource(R.string.addpin_finish))
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPinScreenPreview() {
    WherisTheme {
        AddPinScreen(
            uiState =
                AddPinUiState.PositionFound(
                    location =
                        UserLocation(
                            position = GeoPoint(latitude = 48.8566, longitude = 2.3522),
                            accuracyMeters = 8.5f,
                            altitudeMeters = 35.0,
                            timestampEpochMillis = 1000L,
                        ),
                    isApproximate = false,
                ),
            onRequestPermission = {},
            onOpenSettings = {},
            onOpenLocationSettings = {},
            onRetryLocation = {},
            onConfirmPosition = {},
            onSelectCategory = {},
            onSave = {},
            onBackToPosition = {},
            onFinished = {},
        )
    }
}
