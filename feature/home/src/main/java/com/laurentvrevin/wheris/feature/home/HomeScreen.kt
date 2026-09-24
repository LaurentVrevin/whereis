package com.laurentvrevin.wheris.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.map.WherisMap

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddPlace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        WherisMap(
            markers = uiState.pins.toMapMarkers(),
            modifier = Modifier.fillMaxSize(),
            unavailableContent = {
                MapUnavailableContent(
                    hasSavedPlaces = uiState.pins.isNotEmpty(),
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )

        if (uiState.pins.isEmpty() && !uiState.dataUnavailable) {
            Card(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(WherisSpacing.lg)
                        .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.home_empty_prompt),
                    modifier = Modifier.padding(WherisSpacing.lg),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (uiState.dataUnavailable) {
            Card(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(WherisSpacing.lg)
                        .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.home_data_error),
                    modifier = Modifier.padding(WherisSpacing.lg),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Button(
            onClick = onAddPlace,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = WherisSpacing.lg, vertical = 32.dp)
                    .fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.home_add_place),
                modifier = Modifier.padding(start = WherisSpacing.sm),
            )
        }
    }
}

@Composable
private fun MapUnavailableContent(
    hasSavedPlaces: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(WherisSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.home_map_unavailable),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = WherisSpacing.md),
        )
        Text(
            text =
                if (hasSavedPlaces) {
                    stringResource(R.string.home_map_unavailable_saved)
                } else {
                    stringResource(R.string.home_map_unavailable_empty)
                },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = WherisSpacing.sm),
        )
    }
}
