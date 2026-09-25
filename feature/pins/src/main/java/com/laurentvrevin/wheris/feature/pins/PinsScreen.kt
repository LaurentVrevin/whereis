package com.laurentvrevin.wheris.feature.pins

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.laurentvrevin.wheris.core.ui.category.categoryLabel

@Composable
fun PinsScreen(
    uiState: PinsUiState,
    onPinClick: (PinId) -> Unit,
    onAddPlace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = WherisSpacing.lg),
        ) {
            Text(
                text = stringResource(R.string.pins_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = WherisSpacing.lg, bottom = WherisSpacing.md),
            )

            when (uiState) {
                is PinsUiState.Loading -> {
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

                is PinsUiState.Empty -> {
                    EmptyPlacesContent(
                        onAddPlace = onAddPlace,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }

                is PinsUiState.Content -> {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                    ) {
                        items(
                            items = uiState.pins,
                            key = { it.id.value },
                        ) { pinItem ->
                            PinCardRow(
                                pinItem = pinItem,
                                onClick = { onPinClick(pinItem.id) },
                            )
                        }
                    }
                }

                is PinsUiState.Error -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.pins_error_loading),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPlacesContent(
    onAddPlace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(WherisSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(WherisSpacing.md))
        Text(
            text = stringResource(R.string.pins_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(WherisSpacing.sm))
        Text(
            text = stringResource(R.string.pins_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(WherisSpacing.xl))
        Button(
            onClick = onAddPlace,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.pins_btn_add_place),
                modifier = Modifier.padding(start = WherisSpacing.sm),
            )
        }
    }
}

@Composable
private fun PinCardRow(
    pinItem: PinItemUiState,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                imageVector = categoryIcon(pinItem.pin.categoryId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = WherisSpacing.md),
            ) {
                Text(
                    text = categoryLabel(pinItem.pin.categoryId),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text =
                        "%.5f, %.5f".format(
                            pinItem.pin.position.latitude,
                            pinItem.pin.position.longitude,
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PinsScreenEmptyPreview() {
    WherisTheme {
        PinsScreen(
            uiState = PinsUiState.Empty,
            onPinClick = {},
            onAddPlace = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinsScreenContentPreview() {
    WherisTheme {
        PinsScreen(
            uiState =
                PinsUiState.Content(
                    pins =
                        listOf(
                            PinItemUiState(
                                id = PinId("1"),
                                pin =
                                    Pin(
                                        id = PinId("1"),
                                        position = GeoPoint(48.8566, 2.3522),
                                        categoryId = SystemCategoryIds.CAR,
                                        accuracyMeters = 5.0f,
                                        altitudeMeters = 35.0,
                                        createdAtEpochMillis = 1000L,
                                        updatedAtEpochMillis = 1000L,
                                    ),
                            ),
                            PinItemUiState(
                                id = PinId("2"),
                                pin =
                                    Pin(
                                        id = PinId("2"),
                                        position = GeoPoint(48.8584, 2.2945),
                                        categoryId = SystemCategoryIds.PARKING,
                                        accuracyMeters = 8.0f,
                                        altitudeMeters = 30.0,
                                        createdAtEpochMillis = 2000L,
                                        updatedAtEpochMillis = 2000L,
                                    ),
                            ),
                        ),
                ),
            onPinClick = {},
            onAddPlace = {},
        )
    }
}
