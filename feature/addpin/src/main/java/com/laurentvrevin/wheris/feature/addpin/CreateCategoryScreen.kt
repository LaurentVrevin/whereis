package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryIdentity
import com.laurentvrevin.wheris.core.ui.category.categoryColor
import com.laurentvrevin.wheris.core.ui.category.categoryColorLabelRes
import com.laurentvrevin.wheris.core.ui.category.categoryIcon
import com.laurentvrevin.wheris.core.ui.category.categoryIconLabelRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateCategoryScreen(
    state: AddPinUiState.CategoryCreation,
    onNameChange: (String) -> Unit,
    onIconChange: (CategoryIconKey) -> Unit,
    onColorChange: (CategoryColorKey) -> Unit,
    onCreate: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().imePadding().padding(WherisSpacing.lg)) {
            Text(stringResource(R.string.create_category_title), style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("category_editor"),
                verticalArrangement = Arrangement.spacedBy(WherisSpacing.lg),
            ) {
                item {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        enabled = !state.isCreating,
                        label = { Text(stringResource(R.string.create_category_name)) },
                        supportingText = { Text(stringResource(R.string.create_category_name_required)) },
                        modifier = Modifier.fillMaxWidth().testTag("category_name"),
                    )
                }
                item {
                    Text(stringResource(R.string.create_category_icon), style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(WherisSpacing.sm)) {
                        CategoryIconKey.entries.forEach { key ->
                            FilterChip(
                                selected = state.iconKey == key,
                                onClick = { onIconChange(key) },
                                enabled = !state.isCreating,
                                label = { Text(stringResource(categoryIconLabelRes(key))) },
                                leadingIcon = { Icon(categoryIcon(key), contentDescription = null) },
                                trailingIcon = { if (state.iconKey == key) Icon(Icons.Default.Check, contentDescription = null) },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("icon_${key.value}"),
                            )
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.create_category_color), style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(WherisSpacing.sm)) {
                        CategoryColorKey.entries.forEach { key ->
                            FilterChip(
                                selected = state.colorKey == key,
                                onClick = { onColorChange(key) },
                                enabled = !state.isCreating,
                                label = { Text(stringResource(categoryColorLabelRes(key))) },
                                leadingIcon = {
                                    Surface(
                                        color = categoryColor(key),
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.size(20.dp),
                                    ) {}
                                },
                                trailingIcon = { if (state.colorKey == key) Icon(Icons.Default.Check, contentDescription = null) },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("color_${key.name}"),
                            )
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.create_category_preview), style = MaterialTheme.typography.titleMedium)
                    Card(Modifier.fillMaxWidth().testTag("category_preview")) {
                        WherisCategoryIdentity(
                            label = state.name.trim().ifBlank { stringResource(R.string.create_category_preview_placeholder) },
                            icon = categoryIcon(state.iconKey),
                            accent = categoryColor(state.colorKey),
                        )
                    }
                }
            }
            if (state.creationFailed) {
                Text(
                    stringResource(R.string.create_category_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = WherisSpacing.sm),
                )
            }
            Button(onClick = onCreate, enabled = state.canCreate, modifier = Modifier.fillMaxWidth().testTag("create_category")) {
                if (state.isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.create_category_create))
                }
            }
            TextButton(onClick = onCancel, enabled = !state.isCreating, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.create_category_cancel))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyCategoryPreview() = CategoryEditorPreview()

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, fontScale = 1.5f)
@Composable
private fun FilledCategoryPreview() = CategoryEditorPreview(name = "散歩 — Mes balades au bord de la rivière")

@Preview(showBackground = true)
@Composable
private fun CategoryErrorPreview() = CategoryEditorPreview(name = "Champignons", failed = true)

@Composable
private fun CategoryEditorPreview(
    name: String = "",
    failed: Boolean = false,
) {
    WherisTheme {
        CreateCategoryScreen(
            state =
                AddPinUiState.CategoryCreation(
                    selection = AddPinUiState.CategorySelection(UserLocation(GeoPoint(12.0, 24.0), 8f, 35.0, 1000L)),
                    name = name,
                    iconKey = CategoryIconKey.PARK,
                    colorKey = CategoryColorKey.GREEN,
                    creationFailed = failed,
                ),
            onNameChange = {},
            onIconChange = {},
            onColorChange = {},
            onCreate = {},
            onCancel = {},
        )
    }
}
