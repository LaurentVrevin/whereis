package com.laurentvrevin.wheris.feature.onboarding

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingRoute(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val completionState by viewModel.completionState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasRequested by rememberSaveable { mutableStateOf(false) }
    var permissionState by rememberSaveable { mutableStateOf(LocationPermissionState.NotRequested) }

    fun readPermissionState(): LocationPermissionState {
        val activity = context.findActivity()
        val showRationale =
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } ?: false
        return locationPermissionState(
            fineGranted = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION),
            coarseGranted = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
            hasRequested = hasRequested,
            showRationale = showRationale,
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionState =
                locationPermissionState(
                    fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true,
                    coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true,
                    hasRequested = true,
                    showRationale = readPermissionState() == LocationPermissionState.Denied,
                )
            if (permissionState == LocationPermissionState.Precise || permissionState == LocationPermissionState.Approximate) {
                viewModel.complete()
            }
        }

    DisposableEffect(lifecycleOwner, context) {
        fun refreshPermissions() {
            val current = readPermissionState()
            // A pending system request owns its result; resume must never launch another dialog.
            if (permissionState != LocationPermissionState.Requesting ||
                current == LocationPermissionState.Precise || current == LocationPermissionState.Approximate
            ) {
                permissionState = current
            }
        }
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) refreshPermissions()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) refreshPermissions()
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    OnboardingContent(
        permissionState = permissionState,
        completionState = completionState,
        onRequestPermission = {
            if (permissionState == LocationPermissionState.Requesting ||
                viewModel.completionState.value == OnboardingCompletionState.Saving ||
                viewModel.completionState.value == OnboardingCompletionState.Completed
            ) {
                return@OnboardingContent
            }
            val current = readPermissionState()
            permissionState = current
            when (current) {
                LocationPermissionState.Precise, LocationPermissionState.Approximate -> viewModel.complete()
                LocationPermissionState.SettingsRequired -> Unit
                else -> {
                    hasRequested = true
                    permissionState = LocationPermissionState.Requesting
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    )
                }
            }
        },
        onOpenSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        },
        onComplete = viewModel::complete,
        modifier = modifier,
    )
}

@Composable
internal fun OnboardingContent(
    permissionState: LocationPermissionState,
    completionState: OnboardingCompletionState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var page by rememberSaveable { mutableStateOf(OnboardingPage.WELCOME) }
    val busy =
        completionState == OnboardingCompletionState.Saving || completionState == OnboardingCompletionState.Completed ||
            permissionState == LocationPermissionState.Requesting

    fun goBack() {
        if (!busy) {
            page =
                when (page) {
                    OnboardingPage.WELCOME -> OnboardingPage.WELCOME
                    OnboardingPage.CONCEPT -> OnboardingPage.WELCOME
                    OnboardingPage.PRIVACY -> OnboardingPage.CONCEPT
                    OnboardingPage.LOCATION -> OnboardingPage.PRIVACY
                }
        }
    }

    BackHandler(enabled = page != OnboardingPage.WELCOME || busy) { goBack() }

    OnboardingScreen(
        page = page,
        onContinue = {
            if (!busy) {
                page =
                    when (page) {
                        OnboardingPage.WELCOME -> OnboardingPage.CONCEPT
                        OnboardingPage.CONCEPT -> OnboardingPage.PRIVACY
                        OnboardingPage.PRIVACY -> OnboardingPage.LOCATION
                        OnboardingPage.LOCATION -> OnboardingPage.LOCATION
                    }
            }
        },
        onBack = { goBack() },
        permissionState = permissionState,
        completionState = completionState,
        onRequestPermission = onRequestPermission,
        onOpenSettings = onOpenSettings,
        onComplete = onComplete,
        modifier = modifier,
    )
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
