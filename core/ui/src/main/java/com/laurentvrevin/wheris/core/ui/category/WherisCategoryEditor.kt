package com.laurentvrevin.wheris.core.ui.category

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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.ui.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WherisCategoryEditor(
    name: String,
    iconKey: CategoryIconKey,
    colorKey: CategoryColorKey,
    isBusy: Boolean,
    errorMessage: String?,
    title: String,
    saveLabel: String,
    onNameChange: (String) -> Unit,
    onIconChange: (CategoryIconKey) -> Unit,
    onColorChange: (CategoryColorKey) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    mainActionTag: String = "save_category",
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().imePadding().padding(WherisSpacing.lg)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("category_editor"),
                verticalArrangement = Arrangement.spacedBy(WherisSpacing.lg),
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        enabled = !isBusy,
                        label = { Text(stringResource(R.string.category_editor_name)) },
                        supportingText = { Text(stringResource(R.string.category_editor_name_required)) },
                        modifier = Modifier.fillMaxWidth().testTag("category_name"),
                    )
                }
                item {
                    Text(stringResource(R.string.category_editor_icon), style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(WherisSpacing.sm)) {
                        CategoryIconKey.entries.forEach { key ->
                            FilterChip(
                                selected = iconKey == key,
                                onClick = { onIconChange(key) },
                                enabled = !isBusy,
                                label = { Text(stringResource(categoryIconLabelRes(key))) },
                                leadingIcon = { Icon(categoryIcon(key), contentDescription = null) },
                                trailingIcon = { if (iconKey == key) Icon(Icons.Default.Check, contentDescription = null) },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("icon_${key.value}"),
                            )
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.category_editor_color), style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(WherisSpacing.sm)) {
                        CategoryColorKey.entries.forEach { key ->
                            FilterChip(
                                selected = colorKey == key,
                                onClick = { onColorChange(key) },
                                enabled = !isBusy,
                                label = { Text(stringResource(categoryColorLabelRes(key))) },
                                leadingIcon = {
                                    Surface(
                                        color = categoryColor(key),
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.size(20.dp),
                                    ) {}
                                },
                                trailingIcon = { if (colorKey == key) Icon(Icons.Default.Check, contentDescription = null) },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("color_${key.name}"),
                            )
                        }
                    }
                }
                item {
                    Text(stringResource(R.string.category_editor_preview), style = MaterialTheme.typography.titleMedium)
                    Card(Modifier.fillMaxWidth().testTag("category_preview")) {
                        WherisCategoryIdentity(
                            label = name.trim().ifBlank { stringResource(R.string.category_editor_preview_placeholder) },
                            icon = categoryIcon(iconKey),
                            accent = categoryColor(colorKey),
                        )
                    }
                }
            }
            if (errorMessage != null) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = WherisSpacing.sm).semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            Button(onClick = onSave, enabled = name.isNotBlank() && !isBusy, modifier = Modifier.fillMaxWidth().testTag(mainActionTag)) {
                if (isBusy) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(saveLabel)
                }
            }
            TextButton(onClick = onCancel, enabled = !isBusy, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.category_editor_cancel))
            }
            if (onDelete != null) {
                TextButton(onClick = onDelete, enabled = !isBusy, modifier = Modifier.fillMaxWidth().testTag("delete_category")) {
                    Text(stringResource(R.string.category_editor_delete), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
