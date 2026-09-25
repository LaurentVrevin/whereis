package com.laurentvrevin.wheris.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.laurentvrevin.wheris.core.ui.category.categoryLabel

@Composable
internal fun MapQuickDetailCard(
    pin: Pin,
    distanceMeters: Double?,
    onNavigateClick: () -> Unit,
    onDetailsClick: (PinId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(WherisSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = categoryIcon(pin.categoryId),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp),
                )
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(start = WherisSpacing.md),
                ) {
                    Text(
                        text = categoryLabel(pin.categoryId),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.home_fallback_identity),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            distanceMeters?.let { distance ->
                Text(
                    text = distanceLabel(distance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(WherisSpacing.md),
            ) {
                Button(
                    onClick = onNavigateClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.home_btn_navigate))
                }

                OutlinedButton(
                    onClick = { onDetailsClick(pin.id) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.home_btn_details))
                }
            }
        }
    }
}

@Composable
private fun distanceLabel(distanceMeters: Double): String =
    if (distanceMeters < 1000.0) {
        stringResource(
            R.string.home_distance_meters_value,
            distanceMeters,
        )
    } else {
        stringResource(
            R.string.home_distance_km_value,
            distanceMeters / 1000.0,
        )
    }
