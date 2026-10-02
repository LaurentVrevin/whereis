package com.laurentvrevin.wheris.feature.pindetail

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.core.ui.photo.LocalPhotoPreview
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

/** Tests can replace system interactions with neutral acquisition callbacks. */
data class EditPhotoLaunchers(val choose: () -> Unit, val take: () -> Unit)

@Composable
fun EditPinRoute(
    pinId: PinId,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPinViewModel = koinViewModel(),
    storage: AndroidPhotoStorage = koinInject(),
    photoScope: CoroutineScope = koinInject(qualifier = named("photoOperations")),
    photoLaunchers: EditPhotoLaunchers? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                viewModel.photoCancelled()
            } else {
                photoScope.launch {
                    try {
                        viewModel.photoReady(storage.importPhoto(uri))
                    } catch (
                        exception: CancellationException,
                    ) {
                        viewModel.photoAcquisitionFailed()
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoAcquisitionFailed()
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
                viewModel.photoAcquisitionFailed()
            } else {
                photoScope.launch {
                    try {
                        viewModel.photoReady(storage.validateCamera(draft))
                    } catch (
                        exception: CancellationException,
                    ) {
                        viewModel.photoAcquisitionFailed()
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoAcquisitionFailed()
                    }
                }
            }
        }
    LaunchedEffect(pinId) { viewModel.load(pinId) }
    LaunchedEffect(uiState == EditPinUiState.Saved) { if (uiState == EditPinUiState.Saved) onBack() }

    fun leave() {
        viewModel.abandon()
        onBack()
    }
    BackHandler {
        if ((uiState as? EditPinUiState.Content)?.isSaving != true && uiState != EditPinUiState.Saved) leave()
    }
    EditPinScreen(
        uiState = uiState,
        onNameChange = viewModel::changeName,
        onNoteChange = viewModel::changeNote,
        onCategoryChange = viewModel::selectCategory,
        onFavoriteChange = viewModel::changeFavorite,
        onSave = viewModel::save,
        onBack = ::leave,
        onRetry = { viewModel.load(pinId) },
        onReloadCategories = viewModel::reloadCategories,
        onRemovePhoto = viewModel::removePhoto,
        onChoosePhoto = {
            if (viewModel.beginPhotoAcquisition()) {
                try {
                    if (photoLaunchers != null) {
                        photoLaunchers.choose()
                    } else {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                } catch (
                    exception: CancellationException,
                ) {
                    viewModel.photoAcquisitionFailed()
                    throw exception
                } catch (_: Exception) {
                    viewModel.photoAcquisitionFailed()
                }
            }
        },
        onTakePhoto = {
            if (viewModel.beginPhotoAcquisition()) {
                if (photoLaunchers != null) {
                    try {
                        photoLaunchers.take()
                    } catch (
                        exception: CancellationException,
                    ) {
                        viewModel.photoAcquisitionFailed()
                        throw exception
                    } catch (_: Exception) {
                        viewModel.photoAcquisitionFailed()
                    }
                } else {
                    photoScope.launch {
                        try {
                            val draft = storage.prepareCamera()
                            if (viewModel.cameraPrepared(draft)) camera.launch(storage.cameraUri(draft))
                        } catch (
                            exception: CancellationException,
                        ) {
                            viewModel.photoAcquisitionFailed()
                            throw exception
                        } catch (_: Exception) {
                            viewModel.photoAcquisitionFailed()
                        }
                    }
                }
            }
        },
        photoPreview = {
            val state = uiState as? EditPinUiState.Content
            val replacement = state?.photo as? EditPhotoState.Replacement
            if (replacement != null) {
                LocalPhotoPreview(
                    replacement.draft,
                    { storage.preview(replacement.draft).asImageBitmap() },
                    stringResource(R.string.editpin_replacement_preview),
                    stringResource(R.string.editpin_photo_error),
                    viewModel::previewFailed,
                )
            } else if (state?.photo == EditPhotoState.Unchanged && state.originalPhoto != null) {
                val photo = state.originalPhoto
                LocalPhotoPreview(
                    photo,
                    { storage.preview(photo).asImageBitmap() },
                    stringResource(R.string.editpin_original_preview),
                    stringResource(R.string.editpin_photo_error),
                    viewModel::previewFailed,
                )
            }
        },
        modifier = modifier,
    )
}
