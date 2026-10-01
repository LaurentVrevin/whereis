package com.laurentvrevin.wheris.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryCard
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryEditor
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryIdentity
import com.laurentvrevin.wheris.core.ui.category.categoryColor
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.laurentvrevin.wheris.core.ui.category.categoryLabel

@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onCreate: () -> Unit,
    onEdit: (CategoryId) -> Unit,
    onNameChange: (String) -> Unit,
    onIconChange: (CategoryIconKey) -> Unit,
    onColorChange: (CategoryColorKey) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    onReplacement: (CategoryId) -> Unit,
    onConfirmDelete: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val surface = state.surface) {
            CategorySurface.List -> CategoryList(state, onCreate, onEdit, onBack, onRetry)
            is CategorySurface.Editor ->
                CategoryEditor(
                    surface,
                    state.loadFailed,
                    onNameChange,
                    onIconChange,
                    onColorChange,
                    onSave,
                    onBack,
                    onRequestDelete,
                )
            is CategorySurface.Delete -> {
                if (surface.usageCount == 0) {
                    CategoryEditor(
                        surface.editor,
                        state.loadFailed,
                        onNameChange,
                        onIconChange,
                        onColorChange,
                        onSave,
                        onBack,
                        onRequestDelete,
                    )
                    DeleteUnusedDialog(state, surface, onConfirmDelete, onBack)
                } else {
                    DeleteUsedScreen(state, surface, onReplacement, onConfirmDelete, onBack, onRetry)
                }
            }
        }
    }
}

@Composable
private fun CategoryList(
    state: CategoriesUiState,
    onCreate: () -> Unit,
    onEdit: (CategoryId) -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(WherisSpacing.lg)) {
        TextButton(onClick = onBack) { Text(stringResource(R.string.categories_back)) }
        Text(stringResource(R.string.categories_title), style = MaterialTheme.typography.headlineSmall)
        state.notice?.let { Message(it) }
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.padding(WherisSpacing.lg))
            state.loadFailed -> {
                Message(CategoryMessage.LOAD_FAILED)
                Button(onClick = onRetry) { Text(stringResource(R.string.categories_retry)) }
            }
            else ->
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("categories_list"),
                    verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                ) {
                    item { Text(stringResource(R.string.categories_system), style = MaterialTheme.typography.titleMedium) }
                    items(state.categories.filter { it.isSystem }, key = { it.id.value }) { category ->
                        Card(Modifier.fillMaxWidth().testTag("system_${category.id.value}")) {
                            CategoryIdentity(category)
                            Text(stringResource(R.string.categories_default), Modifier.padding(horizontal = WherisSpacing.md))
                        }
                    }
                    item { Text(stringResource(R.string.categories_custom), style = MaterialTheme.typography.titleMedium) }
                    items(state.categories.filterNot { it.isSystem }, key = { it.id.value }) { category ->
                        val editLabel = stringResource(R.string.categories_edit_named, categoryLabel(category))
                        Card(
                            onClick = { onEdit(category.id) },
                            modifier =
                                Modifier.fillMaxWidth().testTag("edit_${category.id.value}")
                                    .semantics { contentDescription = editLabel },
                        ) {
                            CategoryIdentity(category)
                            Text(stringResource(R.string.categories_edit), Modifier.padding(horizontal = WherisSpacing.md))
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(WherisSpacing.md))
                        }
                    }
                }
        }
        Button(onClick = onCreate, enabled = !state.isLoading && !state.loadFailed, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.categories_create_action))
        }
    }
}

@Composable
private fun CategoryIdentity(category: Category) =
    WherisCategoryIdentity(categoryLabel(category), categoryIcon(category), categoryColor(category))

@Composable
private fun CategoryEditor(
    editor: CategorySurface.Editor,
    loadFailed: Boolean,
    onNameChange: (String) -> Unit,
    onIconChange: (CategoryIconKey) -> Unit,
    onColorChange: (CategoryColorKey) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
) {
    WherisCategoryEditor(
        name = editor.name,
        iconKey = editor.iconKey,
        colorKey = editor.colorKey,
        isBusy = editor.isBusy,
        errorMessage = (editor.error ?: CategoryMessage.LOAD_FAILED.takeIf { loadFailed })?.let { messageText(it) },
        title = stringResource(if (editor.categoryId == null) R.string.categories_create_title else R.string.categories_edit_title),
        saveLabel = stringResource(if (editor.categoryId == null) R.string.categories_create else R.string.categories_save),
        onNameChange = onNameChange,
        onIconChange = onIconChange,
        onColorChange = onColorChange,
        onSave = onSave,
        onCancel = onBack,
        onDelete = onDelete.takeIf { editor.categoryId != null },
    )
}

@Composable
private fun sourceName(
    state: CategoriesUiState,
    deletion: CategorySurface.Delete,
): String = state.categories.firstOrNull { it.id == deletion.editor.categoryId }?.let { categoryLabel(it) } ?: deletion.editor.name

@Composable
private fun DeleteUnusedDialog(
    state: CategoriesUiState,
    deletion: CategorySurface.Delete,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.testTag("delete_unused_dialog"),
        title = { Text(stringResource(R.string.categories_delete_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.categories_delete_unused, sourceName(state, deletion)))
                deletion.error?.let { Message(it) }
                if (deletion.isBusy) CircularProgressIndicator(Modifier.size(24.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !deletion.isBusy, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.categories_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel, enabled = !deletion.isBusy, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.categories_cancel))
            }
        },
    )
}

@Composable
private fun DeleteUsedScreen(
    state: CategoriesUiState,
    deletion: CategorySurface.Delete,
    onReplacement: (CategoryId) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(WherisSpacing.lg)) {
        Text(stringResource(R.string.categories_delete_title), style = MaterialTheme.typography.headlineSmall)
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth().testTag("replacement_list"),
            verticalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
        ) {
            item {
                Text(stringResource(R.string.categories_delete_used, sourceName(state, deletion)))
                Text(pluralStringResource(R.plurals.categories_usage_count, deletion.usageCount, deletion.usageCount))
                Text(stringResource(R.string.categories_replacement), style = MaterialTheme.typography.titleMedium)
            }
            items(state.categories.filterNot { it.id == deletion.editor.categoryId }, key = { it.id.value }) { category ->
                WherisCategoryCard(
                    category,
                    selected = deletion.replacementId == category.id,
                    enabled = !deletion.isBusy,
                    onClick = { onReplacement(category.id) },
                    modifier = Modifier.testTag("replacement_${category.id.value}"),
                )
            }
        }
        if (state.isLoading) CircularProgressIndicator(Modifier.size(24.dp))
        if (state.loadFailed) {
            Message(CategoryMessage.LOAD_FAILED)
            TextButton(onClick = onRetry, enabled = !deletion.isBusy) { Text(stringResource(R.string.categories_retry)) }
        }
        deletion.error?.let { Message(it) }
        Button(
            onClick = onConfirm,
            enabled = !deletion.isBusy && deletion.replacementId != null && !state.isLoading && !state.loadFailed,
            modifier = Modifier.fillMaxWidth().testTag("confirm_reassign"),
        ) {
            if (deletion.isBusy) CircularProgressIndicator(Modifier.size(20.dp))
            Text(stringResource(if (deletion.isBusy) R.string.categories_busy else R.string.categories_confirm_reassign))
        }
        TextButton(onClick = onCancel, enabled = !deletion.isBusy, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.categories_cancel))
        }
    }
}

@Composable
private fun Message(message: CategoryMessage) {
    Text(
        messageText(message),
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(vertical = WherisSpacing.sm).semantics { liveRegion = LiveRegionMode.Polite },
    )
}

@Composable
private fun messageText(message: CategoryMessage): String =
    stringResource(
        when (message) {
            CategoryMessage.LOAD_FAILED -> R.string.categories_load_failed
            CategoryMessage.SAVE_FAILED -> R.string.categories_save_failed
            CategoryMessage.USAGE_FAILED -> R.string.categories_usage_failed
            CategoryMessage.DELETE_FAILED -> R.string.categories_delete_failed
            CategoryMessage.NOT_FOUND -> R.string.categories_not_found
            CategoryMessage.SYSTEM_PROTECTED -> R.string.categories_system_protected
            CategoryMessage.REPLACEMENT_NOT_FOUND -> R.string.categories_replacement_missing
            CategoryMessage.INVALID_REPLACEMENT -> R.string.categories_replacement_invalid
        },
    )
