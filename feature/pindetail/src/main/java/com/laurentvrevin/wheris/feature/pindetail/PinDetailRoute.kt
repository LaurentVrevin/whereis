package com.laurentvrevin.wheris.feature.pindetail

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.map.WherisMap
import com.laurentvrevin.wheris.core.map.toWherisMapMarker
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.navigation.AndroidExternalNavigator
import com.laurentvrevin.wheris.core.navigation.ExternalNavigator
import com.laurentvrevin.wheris.core.navigation.message
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.core.ui.category.categoryLabel
import com.laurentvrevin.wheris.core.ui.photo.LocalPhotoPreview
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PinDetailRoute(
    pinId: PinId,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinDetailViewModel = koinViewModel(),
    storage: AndroidPhotoStorage = koinInject(),
    navigator: ExternalNavigator? = null,
    mapContent: (@Composable (PinDetailUiState.Content) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val externalNavigator = navigator ?: remember(context) { AndroidExternalNavigator(context) }
    var navigationError by remember(pinId) { mutableStateOf<String?>(null) }

    val hasForegroundLocationPermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(pinId, hasForegroundLocationPermission) {
        viewModel.observePin(pinId)
        if (hasForegroundLocationPermission) {
            viewModel.requestCurrentLocation()
        }
    }

    LaunchedEffect(uiState == PinDetailUiState.Deleted) {
        if (uiState == PinDetailUiState.Deleted) onBack()
    }

    BackHandler(
        enabled =
            (uiState as? PinDetailUiState.Content)?.deletion == PinDeletionState.InProgress ||
                (uiState as? PinDetailUiState.CleanupFailed)?.inProgress == true,
    ) {
        // Keep this destination alive until persistence confirms the result.
    }

    PinDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onEdit = onEdit,
        onNavigate = {
            val content = uiState as? PinDetailUiState.Content
            if (content != null && content.deletion == PinDeletionState.None) {
                navigationError = externalNavigator.navigate(content.pin.position).message(context)
            }
        },
        onDelete = viewModel::requestDeletion,
        onConfirmDeletion = viewModel::confirmDeletion,
        onCancelDeletion = viewModel::cancelDeletion,
        modifier = modifier,
        navigationError = navigationError,
        mapContent =
            mapContent ?: { content ->
                val description =
                    stringResource(
                        R.string.pindetail_map_description,
                        categoryLabel(content.pin.categoryId, content.category),
                    )
                WherisMap(
                    markers = listOf(content.pin.toWherisMapMarker(isSelected = true).copy(category = content.category)),
                    focus = content.pin.position,
                    modifier = Modifier.fillMaxWidth().height(220.dp).semantics { contentDescription = description },
                    unavailableContent = { retry -> DetailMapUnavailable(retry) },
                )
            },
        photoContent = { reference ->
            LocalPhotoPreview(
                key = reference,
                load = { storage.preview(reference).asImageBitmap() },
                description = stringResource(R.string.pindetail_photo_description),
                failureText = stringResource(R.string.pindetail_photo_unavailable),
                onFailure = {},
            )
        },
    )
}
