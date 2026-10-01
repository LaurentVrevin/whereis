package com.laurentvrevin.wheris.feature.categories

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler(enabled = state.surface != CategorySurface.List) { viewModel.back() }
    CategoriesScreen(
        state = state,
        onCreate = viewModel::openCreate,
        onEdit = viewModel::openEdit,
        onNameChange = viewModel::updateName,
        onIconChange = viewModel::selectIcon,
        onColorChange = viewModel::selectColor,
        onSave = viewModel::save,
        onRequestDelete = viewModel::requestDelete,
        onReplacement = viewModel::selectReplacement,
        onConfirmDelete = viewModel::confirmDelete,
        onBack = { if (state.surface == CategorySurface.List) onBack() else viewModel.back() },
        onRetry = viewModel::retryObservation,
        modifier = modifier,
    )
}
