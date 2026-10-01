package com.laurentvrevin.wheris.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

@Composable
internal fun LocationContent(
    permissionState: LocationPermissionState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(WherisSpacing.lg)) {
        Text(stringResource(R.string.onboarding_location_accuracy), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.onboarding_location_tracking), style = MaterialTheme.typography.bodyLarge)
        val message =
            when (permissionState) {
                LocationPermissionState.Precise -> R.string.onboarding_location_precise
                LocationPermissionState.Approximate -> R.string.onboarding_location_approximate
                LocationPermissionState.Denied, LocationPermissionState.SettingsRequired -> R.string.onboarding_location_denied
                else -> null
            }
        if (message != null) {
            Text(
                stringResource(message),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

@Composable
internal fun LocationActions(
    permissionState: LocationPermissionState,
    completionState: OnboardingCompletionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val denied = permissionState == LocationPermissionState.Denied || permissionState == LocationPermissionState.SettingsRequired
    val granted = permissionState == LocationPermissionState.Precise || permissionState == LocationPermissionState.Approximate
    val saving = completionState == OnboardingCompletionState.Saving || completionState == OnboardingCompletionState.Completed
    val enabled = !saving && permissionState != LocationPermissionState.Requesting
    val label =
        when {
            saving -> R.string.onboarding_saving
            granted -> R.string.onboarding_finish
            permissionState == LocationPermissionState.SettingsRequired -> R.string.onboarding_open_settings
            denied -> R.string.onboarding_permission_retry
            else -> R.string.onboarding_allow_location
        }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm)) {
        Button(
            onClick = {
                when {
                    granted -> onComplete()
                    permissionState == LocationPermissionState.SettingsRequired -> onOpenSettings()
                    else -> onRequestPermission()
                }
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().heightIn(min = WherisSpacing.xxxxl),
        ) {
            Text(stringResource(label))
        }
        if (denied) {
            TextButton(
                onClick = onComplete,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().heightIn(min = WherisSpacing.xxxxl),
            ) {
                Text(stringResource(R.string.onboarding_continue_without_location))
            }
        }
    }
}
