package com.laurentvrevin.wheris.feature.addpin

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
fun AddPinRoute(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddPinViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context.findActivity()

    fun evaluatePermissionDenied() {
        val showRationale =
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } ?: false

        viewModel.onPermissionDenied(isPermanentlyDenied = !showRationale)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isFineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val isCoarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isFineGranted || isCoarseGranted) {
                viewModel.onPermissionGranted(
                    isFineGranted = isFineGranted,
                    isCoarseGranted = isCoarseGranted,
                )
            } else {
                evaluatePermissionDenied()
            }
        }

    fun checkAndRequestPermissions() {
        val hasFine = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarse = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFine || hasCoarse) {
            viewModel.onPermissionGranted(
                isFineGranted = hasFine,
                isCoarseGranted = hasCoarse,
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val hasFine = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    val hasCoarse = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

                    if (hasFine || hasCoarse) {
                        viewModel.onPermissionGranted(
                            isFineGranted = hasFine,
                            isCoarseGranted = hasCoarse,
                        )
                    } else if (viewModel.uiState.value is AddPinUiState.PermissionDenied) {
                        evaluatePermissionDenied()
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = uiState is AddPinUiState.CategorySelection) {
        viewModel.backToPosition()
    }
    BackHandler(enabled = uiState is AddPinUiState.Saved) {
        onFinished()
    }

    AddPinScreen(
        uiState = uiState,
        onRequestPermission = { checkAndRequestPermissions() },
        onOpenSettings = { openAppSettings(context) },
        onOpenLocationSettings = { openLocationSettings(context) },
        onRetryLocation = { checkAndRequestPermissions() },
        onConfirmPosition = viewModel::confirmPosition,
        onSelectCategory = viewModel::selectCategory,
        onSave = viewModel::savePin,
        onBackToPosition = viewModel::backToPosition,
        onFinished = onFinished,
        modifier = modifier,
    )
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

private fun openAppSettings(context: Context) {
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    context.startActivity(intent)
}

private fun openLocationSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    context.startActivity(intent)
}
