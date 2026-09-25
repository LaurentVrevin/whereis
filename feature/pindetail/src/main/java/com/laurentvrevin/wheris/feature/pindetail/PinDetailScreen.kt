package com.laurentvrevin.wheris.feature.pindetail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.laurentvrevin.wheris.core.ui.category.categoryLabel

@Composable
fun PinDetailScreen(
    uiState: PinDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (uiState) {
            PinDetailUiState.Loading ->
                LoadingContent()

            PinDetailUiState.NotFound ->
                MessageContent(
                    title = stringResource(R.string.pindetail_not_found_title),
                    description = stringResource(R.string.pindetail_not_found_description),
                    onBack = onBack,
                )

            PinDetailUiState.Error ->
                MessageContent(
                    title = stringResource(R.string.pindetail_error_title),
                    description = stringResource(R.string.pindetail_error_description),
                    onBack = onBack,
                )

            is PinDetailUiState.Content ->
                Content(
                    state = uiState,
                    onBack = onBack,
                )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    title: String,
    description: String,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(WherisSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(WherisSpacing.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(WherisSpacing.lg))
        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.pindetail_btn_back))
        }
    }
}

@Composable
private fun Content(
    state: PinDetailUiState.Content,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                start = WherisSpacing.lg,
                end = WherisSpacing.lg,
                top = WherisSpacing.md,
                bottom = WherisSpacing.xl,
            ),
        verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.pindetail_btn_back),
                    )
                }
                Text(
                    text = stringResource(R.string.pindetail_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = WherisSpacing.sm),
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(WherisSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = categoryIcon(state.pin.categoryId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    )
                    Column(modifier = Modifier.padding(start = WherisSpacing.md)) {
                        Text(
                            text = categoryLabel(state.pin.categoryId),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stringResource(R.string.pindetail_fallback_identity),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(WherisSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                ) {
                    Text(
                        text = stringResource(R.string.pindetail_coordinates_title),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    InfoRow(
                        label = stringResource(R.string.pindetail_coordinates_label),
                        value =
                            stringResource(
                                R.string.pindetail_coordinates_format,
                                state.pin.position.latitude,
                                state.pin.position.longitude,
                            ),
                    )

                    state.pin.accuracyMeters?.let { accuracy ->
                        InfoRow(
                            label = stringResource(R.string.pindetail_accuracy_label),
                            value =
                                stringResource(
                                    R.string.pindetail_accuracy_value,
                                    accuracy,
                                ),
                        )
                    }

                    state.pin.altitudeMeters?.let { altitude ->
                        InfoRow(
                            label = stringResource(R.string.pindetail_altitude_label),
                            value =
                                stringResource(
                                    R.string.pindetail_altitude_value,
                                    altitude,
                                ),
                        )
                    }
                }
            }
        }

        if (state.distanceMeters != null || state.cardinalDirection != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(WherisSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                    ) {
                        Text(
                            text = stringResource(R.string.pindetail_from_current_location),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        state.distanceMeters?.let { distance ->
                            InfoRow(
                                label = stringResource(R.string.pindetail_distance_label),
                                value = distanceLabel(distance),
                            )
                        }

                        state.cardinalDirection?.let { direction ->
                            InfoRow(
                                label = stringResource(R.string.pindetail_direction_label),
                                value = cardinalDirectionLabel(direction),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f),
        )
    }
}

@Composable
private fun distanceLabel(distanceMeters: Double): String =
    if (distanceMeters < 1000.0) {
        stringResource(
            R.string.pindetail_distance_meters_value,
            distanceMeters,
        )
    } else {
        stringResource(
            R.string.pindetail_distance_km_value,
            distanceMeters / 1000.0,
        )
    }

@Composable
private fun cardinalDirectionLabel(direction: CardinalDirection): String =
    stringResource(
        when (direction) {
            CardinalDirection.NORTH -> R.string.cardinal_north
            CardinalDirection.NORTH_EAST -> R.string.cardinal_north_east
            CardinalDirection.EAST -> R.string.cardinal_east
            CardinalDirection.SOUTH_EAST -> R.string.cardinal_south_east
            CardinalDirection.SOUTH -> R.string.cardinal_south
            CardinalDirection.SOUTH_WEST -> R.string.cardinal_south_west
            CardinalDirection.WEST -> R.string.cardinal_west
            CardinalDirection.NORTH_WEST -> R.string.cardinal_north_west
        },
    )

@Preview(showBackground = true)
@Composable
private fun PinDetailContentPreview() {
    WherisTheme {
        PinDetailScreen(
            uiState =
                PinDetailUiState.Content(
                    pin =
                        Pin(
                            id = PinId("preview-pin"),
                            position = GeoPoint(12.0, 24.0),
                            categoryId = SystemCategoryIds.PARKING,
                            accuracyMeters = 8f,
                            altitudeMeters = 42.0,
                            createdAtEpochMillis = 1_000L,
                            updatedAtEpochMillis = 1_000L,
                        ),
                    distanceMeters = 850.0,
                    cardinalDirection = CardinalDirection.NORTH_EAST,
                ),
            onBack = {},
        )
    }
}
