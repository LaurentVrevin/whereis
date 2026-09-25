package com.laurentvrevin.wheris.feature.pindetail

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
import org.koin.androidx.compose.koinViewModel

@Composable
fun PinDetailRoute(
    pinId: PinId,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    PinDetailScreen(
        uiState = uiState,
        onBack = onBack,
        modifier = modifier,
    )
}
