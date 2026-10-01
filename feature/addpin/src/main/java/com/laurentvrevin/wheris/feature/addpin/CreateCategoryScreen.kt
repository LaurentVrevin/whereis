package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.ui.category.WherisCategoryEditor

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
    WherisCategoryEditor(
        name = state.name,
        iconKey = state.iconKey,
        colorKey = state.colorKey,
        isBusy = state.isCreating,
        errorMessage = if (state.creationFailed) stringResource(R.string.create_category_error) else null,
        title = stringResource(R.string.create_category_title),
        saveLabel = stringResource(R.string.create_category_create),
        onNameChange = onNameChange,
        onIconChange = onIconChange,
        onColorChange = onColorChange,
        onSave = onCreate,
        onCancel = onCancel,
        modifier = modifier,
        mainActionTag = "create_category",
    )
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
