package com.laurentvrevin.wheris.feature.pins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
import org.koin.androidx.compose.koinViewModel

@Composable
fun PinsRoute(
    onPinClick: (PinId) -> Unit,
    onAddPlace: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PinsScreen(
        uiState = uiState,
        onPinClick = onPinClick,
        onAddPlace = onAddPlace,
        modifier = modifier,
    )
}
