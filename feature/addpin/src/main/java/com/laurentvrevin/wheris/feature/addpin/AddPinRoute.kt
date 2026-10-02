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
import androidx.activity.result.PickVisualMediaRequest
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
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

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
    val photoStorage = koinInject<AndroidPhotoStorage>()
    // Acquisition copying survives configuration changes without retaining an Activity.
    val photoScope = koinInject<CoroutineScope>(qualifier = named("photoOperations"))
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                viewModel.photoCancelled()
            } else {
                photoScope.launch {
                    try {
                        viewModel.photoReady(photoStorage.importPhoto(uri))
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoFailed()
                    }
                }
            }
        }
    val camera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { succeeded ->
            val draft = viewModel.cameraDraft
            if (!succeeded) {
                viewModel.photoCancelled()
            } else if (draft == null) {
                viewModel.photoFailed()
            } else {
                photoScope.launch {
                    try {
                        viewModel.photoReady(photoStorage.validateCamera(draft))
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoFailed()
                    }
                }
            }
        }

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

    fun checkAndRequestPermissions(retryLocation: Boolean = false) {
        val hasFine = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarse = context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFine || hasCoarse) {
            if (retryLocation) {
                viewModel.retryLocation(isFineGranted = hasFine, isCoarseGranted = hasCoarse)
            } else {
                viewModel.onPermissionGranted(isFineGranted = hasFine, isCoarseGranted = hasCoarse)
            }
        } else {
            if (retryLocation) {
                viewModel.retryLocation(isFineGranted = false, isCoarseGranted = false)
            }
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
        if ((uiState as? AddPinUiState.CategorySelection)?.isSaving == false) {
            viewModel.backToPosition()
        }
    }
    BackHandler(enabled = uiState is AddPinUiState.CategoryCreation) {
        viewModel.cancelCategoryCreation()
    }
    BackHandler(enabled = uiState is AddPinUiState.Details) { viewModel.backFromDetails() }
    BackHandler(enabled = uiState is AddPinUiState.Saved) {
        onFinished()
    }

    AddPinScreen(
        uiState = uiState,
        onRequestPermission = { checkAndRequestPermissions() },
        onOpenSettings = { openAppSettings(context) },
        onOpenLocationSettings = { openLocationSettings(context) },
        onRetryLocation = { checkAndRequestPermissions(retryLocation = true) },
        onConfirmPosition = viewModel::confirmPosition,
        onSelectCategory = viewModel::selectCategory,
        onSave = viewModel::savePin,
        onBackToPosition = viewModel::backToPosition,
        onFinished = onFinished,
        onOpenCategoryCreation = viewModel::openCategoryCreation,
        onCategoryNameChange = viewModel::updateCategoryName,
        onCategoryIconChange = viewModel::selectCategoryIcon,
        onCategoryColorChange = viewModel::selectCategoryColor,
        onCreateCategory = viewModel::createCategory,
        onCancelCategoryCreation = viewModel::cancelCategoryCreation,
        onOpenDetails = viewModel::openDetails,
        onBackFromDetails = viewModel::backFromDetails,
        onNameChange = viewModel::updateName,
        onNoteChange = viewModel::updateNote,
        onFavoriteChange = viewModel::updateFavorite,
        onRemovePhoto = viewModel::removePhoto,
        onChoosePhoto = {
            if (viewModel.beginPhotoAcquisition()) {
                try {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } catch (_: Exception) {
                    viewModel.photoFailed()
                }
            }
        },
        onTakePhoto = {
            if (viewModel.beginPhotoAcquisition()) {
                photoScope.launch {
                    try {
                        val draft = photoStorage.prepareCamera()
                        if (viewModel.onCameraPrepared(draft)) camera.launch(photoStorage.cameraUri(draft))
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoFailed()
                    }
                }
            }
        },
        photoPreview = {
            (uiState as? AddPinUiState.Details)?.selection?.details?.photo?.let {
                LocalPhotoPreview(it, photoStorage, viewModel::photoFailed)
            }
        },
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
