package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

@Composable
fun AddPinScreen(
    uiState: AddPinUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
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

            when (uiState) {
                is AddPinUiState.PermissionRequired -> {
                    PermissionRequiredContent(onRequestPermission = onRequestPermission)
                }
                is AddPinUiState.PermissionDenied -> {
                    PermissionDeniedContent(
                        onOpenSettings = onOpenSettings,
                        onRetry = onRetry,
                    )
                }
                is AddPinUiState.Searching -> {
                    SearchingContent()
                }
                is AddPinUiState.Success -> {
                    SuccessContent(
                        state = uiState,
                        onRetry = onRetry,
                    )
                }
                is AddPinUiState.ServicesDisabled -> {
                    ServicesDisabledContent(
                        onOpenLocationSettings = onOpenLocationSettings,
                        onRetry = onRetry,
                    )
                }
                is AddPinUiState.Timeout -> {
                    TimeoutContent(onRetry = onRetry)
                }
                is AddPinUiState.Error -> {
                    ErrorContent(
                        message = uiState.message,
                        onRetry = onRetry,
                    )
                }
            }
        }
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
            Spacer(modifier = Modifier.height(WherisSpacing.md))
            Text(
                text = stringResource(R.string.addpin_permission_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(WherisSpacing.sm))
            Text(
                text = stringResource(R.string.addpin_permission_rationale),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(WherisSpacing.lg))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.addpin_btn_grant_permission))
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(WherisSpacing.md))
        Text(
            text = stringResource(R.string.addpin_permission_denied_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        Text(
            text = stringResource(R.string.addpin_permission_denied_msg),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.lg))
        Button(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_open_settings))
        }
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_retry))
        }
    }
}

@Composable
private fun SearchingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(WherisSpacing.lg))
        Text(
            text = stringResource(R.string.addpin_searching_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.xs))
        Text(
            text = stringResource(R.string.addpin_searching_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SuccessContent(
    state: AddPinUiState.Success,
    onRetry: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(WherisSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(WherisSpacing.md))
            Text(
                text = stringResource(R.string.addpin_success_title),
                style = MaterialTheme.typography.titleLarge,
            )
            if (state.isApproximate) {
                Spacer(modifier = Modifier.height(WherisSpacing.xs))
                Text(
                    text = stringResource(R.string.addpin_approximate_badge),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            state.location.accuracyMeters?.let { accuracy ->
                Spacer(modifier = Modifier.height(WherisSpacing.sm))
                Text(
                    text = stringResource(R.string.addpin_accuracy_format, accuracy),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            state.location.altitudeMeters?.let { altitude ->
                Spacer(modifier = Modifier.height(WherisSpacing.xs))
                Text(
                    text = stringResource(R.string.addpin_altitude_format, altitude),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(WherisSpacing.lg))
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.addpin_btn_retry))
            }
        }
    }
}

@Composable
private fun ServicesDisabledContent(
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(WherisSpacing.md))
        Text(
            text = stringResource(R.string.addpin_services_disabled_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        Text(
            text = stringResource(R.string.addpin_services_disabled_msg),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.lg))
        Button(
            onClick = onOpenLocationSettings,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_open_location_settings))
        }
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_retry))
        }
    }
}

@Composable
private fun TimeoutContent(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(WherisSpacing.md))
        Text(
            text = stringResource(R.string.addpin_timeout_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        Text(
            text = stringResource(R.string.addpin_timeout_msg),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.lg))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_retry))
        }
    }
}

@Composable
private fun ErrorContent(
    message: String?,
    onRetry: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(WherisSpacing.md))
        Text(
            text = stringResource(R.string.addpin_error_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.sm))
        Text(
            text = message ?: stringResource(R.string.addpin_error_msg),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(WherisSpacing.lg))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.addpin_btn_retry))
        }
    }
}
