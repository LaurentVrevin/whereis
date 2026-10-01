package com.laurentvrevin.wheris.feature.onboarding

enum class LocationPermissionState {
    NotRequested,
    Requesting,
    Precise,
    Approximate,
    Denied,
    SettingsRequired,
}

internal fun locationPermissionState(
    fineGranted: Boolean,
    coarseGranted: Boolean,
    hasRequested: Boolean,
    showRationale: Boolean,
): LocationPermissionState =
    when {
        fineGranted -> LocationPermissionState.Precise
        coarseGranted -> LocationPermissionState.Approximate
        !hasRequested -> LocationPermissionState.NotRequested
        showRationale -> LocationPermissionState.Denied
        else -> LocationPermissionState.SettingsRequired
    }
