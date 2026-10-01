package com.laurentvrevin.wheris.feature.pindetail

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditPinRoute(
    pinId: PinId,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPinViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(pinId) { viewModel.load(pinId) }
    LaunchedEffect(uiState == EditPinUiState.Saved) {
        if (uiState == EditPinUiState.Saved) onBack()
    }
    BackHandler(enabled = (uiState as? EditPinUiState.Content)?.isSaving == true || uiState == EditPinUiState.Saved) {
        // Wait for the result before leaving the editor.
    }
    EditPinScreen(
        uiState = uiState,
        onNameChange = viewModel::changeName,
        onNoteChange = viewModel::changeNote,
        onSave = viewModel::save,
        onBack = onBack,
        onRetry = { viewModel.load(pinId) },
        modifier = modifier,
    )
}
