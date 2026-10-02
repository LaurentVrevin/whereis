package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryCard
import com.laurentvrevin.wheris.core.ui.category.categoryLabel
import com.laurentvrevin.wheris.core.ui.photo.SyntheticPhotoPreview
import com.laurentvrevin.wheris.domain.PinUpdateResult

@Composable
fun EditPinScreen(
    uiState: EditPinUiState,
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (CategoryId) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onChoosePhoto: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onReloadCategories: () -> Unit,
    modifier: Modifier = Modifier,
    photoPreview: @Composable () -> Unit = {},
) {
    val state = uiState as? EditPinUiState.Content
    val busy = state?.isSaving == true || uiState == EditPinUiState.Saved
    val editable = !busy && state?.saveError != PinUpdateResult.PIN_NOT_FOUND
    Surface(modifier = modifier.fillMaxSize()) {
        Column(Modifier.imePadding().padding(WherisSpacing.lg)) {
            TextButton(onClick = onBack, enabled = !busy, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.pindetail_btn_back))
            }
            Text(stringResource(R.string.editpin_title), style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth().testTag("edit_fields"),
                verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
            ) {
                when (uiState) {
                    EditPinUiState.Loading, EditPinUiState.Saved -> item { CircularProgressIndicator(Modifier.testTag("edit_loading")) }
                    EditPinUiState.NotFound -> item { Text(stringResource(R.string.pindetail_not_found_description)) }
                    EditPinUiState.LoadFailed ->
                        item {
                            ErrorText(stringResource(R.string.editpin_load_error))
                            Button(onClick = onRetry) { Text(stringResource(R.string.editpin_retry)) }
                        }
                    is EditPinUiState.Content -> {
                        item {
                            OutlinedTextField(
                                uiState.name,
                                onNameChange,
                                label = { Text(stringResource(R.string.editpin_name)) },
                                singleLine = true,
                                enabled = editable,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        item {
                            Text(stringResource(R.string.editpin_category), style = MaterialTheme.typography.titleMedium)
                            uiState.categories.firstOrNull { it.id == uiState.selectedCategoryId }?.let {
                                Text(categoryLabel(it), modifier = Modifier.testTag("edit_selected_category"))
                            }
                            LazyRow(
                                Modifier.fillMaxWidth().testTag("edit_categories"),
                                horizontalArrangement = Arrangement.spacedBy(WherisSpacing.sm),
                            ) {
                                items(uiState.categories, key = { it.id.value }) { category ->
                                    WherisCategoryCard(
                                        category,
                                        category.id == uiState.selectedCategoryId,
                                        editable,
                                        { onCategoryChange(category.id) },
                                        Modifier.width(200.dp).testTag("edit_category_${category.id.value}"),
                                    )
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                uiState.note,
                                onNoteChange,
                                label = { Text(stringResource(R.string.editpin_note)) },
                                minLines = 3,
                                maxLines = 6,
                                enabled = editable,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        item {
                            Row(
                                Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(
                                    uiState.isFavorite,
                                    enabled = editable,
                                    role = Role.Checkbox,
                                    onValueChange = onFavoriteChange,
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(uiState.isFavorite, onCheckedChange = null)
                                Text(stringResource(R.string.editpin_favorite))
                            }
                        }
                        item { Text(stringResource(R.string.editpin_photo), style = MaterialTheme.typography.titleMedium) }
                        if (uiState.hasPhoto) {
                            item { photoPreview() }
                            item {
                                OutlinedButton(
                                    onRemovePhoto,
                                    enabled = editable && !uiState.isAcquiringPhoto,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                ) {
                                    Text(
                                        stringResource(
                                            if (uiState.photo is EditPhotoState.Replacement) {
                                                R.string.editpin_remove_replacement
                                            } else {
                                                R.string.editpin_remove_photo
                                            },
                                        ),
                                    )
                                }
                            }
                        }
                        item {
                            OutlinedButton(
                                onChoosePhoto,
                                enabled = editable && !uiState.isAcquiringPhoto,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            ) {
                                Text(stringResource(R.string.editpin_choose_photo))
                            }
                        }
                        item {
                            OutlinedButton(
                                onTakePhoto,
                                enabled = editable && !uiState.isAcquiringPhoto,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            ) {
                                Text(stringResource(R.string.editpin_take_photo))
                            }
                        }
                        if (uiState.isAcquiringPhoto) {
                            item {
                                Text(stringResource(R.string.editpin_acquiring))
                                CircularProgressIndicator()
                            }
                        }
                        if (uiState.photoFailed) item { ErrorText(stringResource(R.string.editpin_photo_error)) }
                        if (uiState.categoriesFailed || uiState.categories.none { it.id == uiState.selectedCategoryId } ||
                            uiState.saveError == PinUpdateResult.CATEGORY_NOT_FOUND
                        ) {
                            item {
                                ErrorText(stringResource(R.string.editpin_category_error))
                                TextButton(onReloadCategories, enabled = !busy) { Text(stringResource(R.string.editpin_reload_categories)) }
                            }
                        }
                        uiState.saveError?.takeUnless { it == PinUpdateResult.CATEGORY_NOT_FOUND }?.let { result ->
                            item {
                                ErrorText(
                                    stringResource(
                                        when (result) {
                                            PinUpdateResult.PIN_NOT_FOUND -> R.string.editpin_missing
                                            PinUpdateResult.CATEGORY_NOT_FOUND -> R.string.editpin_category_error
                                            PinUpdateResult.PHOTO_ALREADY_ATTACHED -> R.string.editpin_photo_attached
                                            else -> R.string.editpin_save_error
                                        },
                                    ),
                                )
                            }
                        }
                    }
                }
            }
            if (state != null) {
                Button(onSave, enabled = state.canSave, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(if (state.isSaving) R.string.editpin_saving else R.string.editpin_save))
                }
            }
            OutlinedButton(onBack, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.pindetail_cancel))
            }
        }
    }
}

@Composable
private fun ErrorText(text: String) =
    Text(
        text,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
    )

@Preview(showBackground = true)
@Composable
private fun PlainEditPreview() = EditPreview()

@Preview(showBackground = true)
@Composable
private fun PhotoEditPreview() = EditPreview(photo = PhotoReference("preview"))

@Preview(showBackground = true)
@Composable
private fun FavoriteEditPreview() = EditPreview(favorite = true)

@Preview(showBackground = true)
@Composable
private fun LongNoteEditPreview() = EditPreview(note = "Une note avec plusieurs lignes\n".repeat(16))

@Preview(showBackground = true)
@Composable
private fun SaveErrorEditPreview() = EditPreview(error = PinUpdateResult.TECHNICAL_FAILURE)

@Preview(showBackground = true)
@Composable
private fun MissingCategoryEditPreview() = EditPreview(error = PinUpdateResult.CATEGORY_NOT_FOUND)

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkEditPreview() = EditPreview(favorite = true, photo = PhotoReference("preview"))

@Preview(showBackground = true, fontScale = 1.5f)
@Composable
private fun LargeEditPreview() = EditPreview(note = "Une note longue\n".repeat(16))

@Preview(showBackground = true)
@Composable
private fun ReplacementEditPreview() = EditPreview(replacement = true)

@Composable
private fun EditPreview(
    photo: PhotoReference? = null,
    favorite: Boolean = false,
    note: String = "Une promenade à retrouver",
    error: PinUpdateResult? = null,
    replacement: Boolean = false,
) {
    WherisTheme {
        EditPinScreen(
            EditPinUiState.Content(
                PinId("preview"), "Au bord de l’eau", note,
                listOf(Category(SystemCategoryIds.PARKING, true), Category(SystemCategoryIds.OTHER, true)),
                if (error == PinUpdateResult.CATEGORY_NOT_FOUND) CategoryId("missing") else SystemCategoryIds.PARKING,
                favorite, photo,
                photo =
                    if (replacement) {
                        EditPhotoState.Replacement(
                            PhotoDraftReference("draft"),
                        )
                    } else {
                        EditPhotoState.Unchanged
                    },
                saveError = error,
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, photoPreview = { SyntheticPhotoPreview() },
        )
    }
}
