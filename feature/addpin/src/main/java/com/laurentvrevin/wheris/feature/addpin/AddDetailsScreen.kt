package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryCard

@Composable
fun AddDetailsScreen(
    state: AddPinUiState.CategorySelection,
    onNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onChoosePhoto: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    photoPreview: @Composable () -> Unit = {},
) {
    val details = state.details
    val busy = state.isSaving || details.isAcquiringPhoto
    Column(modifier.fillMaxSize().imePadding().padding(WherisSpacing.lg)) {
        TextButton(onClick = onBack, enabled = !busy, modifier = Modifier.heightIn(min = 48.dp)) {
            Text(stringResource(R.string.details_back))
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("details_fields"),
            verticalArrangement = Arrangement.spacedBy(WherisSpacing.md),
        ) {
            item { Text(stringResource(R.string.details_title), style = MaterialTheme.typography.headlineSmall) }
            item {
                state.categories.firstOrNull { it.id == state.selectedCategoryId }?.let { category ->
                    WherisCategoryCard(category = category, selected = true, enabled = false, onClick = {})
                }
            }
            item {
                OutlinedTextField(
                    value = details.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.details_name)) },
                    singleLine = true,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = details.note,
                    onValueChange = onNoteChange,
                    label = { Text(stringResource(R.string.details_note)) },
                    minLines = 3,
                    maxLines = 6,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item { Text(stringResource(R.string.details_photo), style = MaterialTheme.typography.titleMedium) }
            if (details.photo != null) {
                item { photoPreview() }
                item {
                    OutlinedButton(onClick = onRemovePhoto, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.details_remove_photo))
                    }
                }
            }
            item {
                OutlinedButton(onClick = onChoosePhoto, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.details_choose_photo))
                }
            }
            item {
                OutlinedButton(onClick = onTakePhoto, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.details_take_photo))
                }
            }
            if (details.isAcquiringPhoto) {
                item {
                    Text(stringResource(R.string.details_acquiring))
                    CircularProgressIndicator()
                }
            }
            if (details.photoFailed) {
                item {
                    Text(
                        stringResource(R.string.details_photo_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
            }
            item {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(
                            value = details.isFavorite,
                            enabled = !state.isSaving,
                            role = Role.Checkbox,
                            onValueChange = onFavoriteChange,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = details.isFavorite, onCheckedChange = null)
                    Text(stringResource(R.string.details_favorite))
                }
            }
            if (state.saveFailed) {
                item {
                    Text(
                        stringResource(R.string.addpin_save_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
            }
        }
        Button(
            onClick = onSave,
            enabled = !busy && state.categories.any { it.id == state.selectedCategoryId },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = WherisSpacing.sm),
        ) {
            Text(stringResource(if (state.isSaving) R.string.addpin_saving else R.string.addpin_btn_save))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyDetailsPreview() = DetailsPreview()

@Preview(showBackground = true)
@Composable
private fun EnrichedDetailsPreview() =
    DetailsPreview(
        PlaceDetailsDraft("Au bord de l’eau", "Une promenade\nÀ retrouver au printemps", true),
    )

@Preview(showBackground = true)
@Composable
private fun PhotoDetailsPreview() = DetailsPreview(PlaceDetailsDraft(photo = PhotoDraftReference("preview")))

@Preview(showBackground = true)
@Composable
private fun PhotoFailurePreview() = DetailsPreview(PlaceDetailsDraft(photoFailed = true))

@Preview(showBackground = true)
@Composable
private fun SaveFailurePreview() = DetailsPreview(failed = true)

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, fontScale = 1.5f)
@Composable
private fun LargeDarkDetailsPreview() = DetailsPreview(PlaceDetailsDraft(note = "Une note avec plusieurs lignes\n".repeat(12)))

@Composable
private fun DetailsPreview(
    details: PlaceDetailsDraft = PlaceDetailsDraft(),
    failed: Boolean = false,
) {
    WherisTheme {
        AddDetailsScreen(
            AddPinUiState.CategorySelection(
                UserLocation(GeoPoint(12.0, 24.0), timestampEpochMillis = 1000L),
                categories = listOf(Category(SystemCategoryIds.PARKING, true)),
                selectedCategoryId = SystemCategoryIds.PARKING,
                isLoadingCategories = false,
                details = details,
                saveFailed = failed,
            ),
            {}, {}, {}, {}, {}, {}, {}, {},
            photoPreview = { SyntheticPhotoPreview() },
        )
    }
}
