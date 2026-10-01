package com.laurentvrevin.wheris.feature.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds

private val previewCustom =
    Category(
        CategoryId("walks"),
        false,
        "散歩 — Mes longues balades au bord de la rivière",
        CategoryIconKey.PARK,
        CategoryColorKey.PURPLE,
        1000L,
    )
private val previewCategories = listOf(SystemCategoryIds.CAR, SystemCategoryIds.OTHER).map { Category(it, true) } + previewCustom
private val previewEditor =
    CategorySurface.Editor(
        previewCustom.id,
        requireNotNull(previewCustom.name),
        requireNotNull(previewCustom.iconKey),
        requireNotNull(previewCustom.colorKey),
    )

@Preview(showBackground = true)
@Composable
private fun CategoryListPreview() = CategoriesPreview(CategorySurface.List)

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, fontScale = 1.5f)
@Composable
private fun LongNamesDarkPreview() = CategoriesPreview(CategorySurface.List, dark = true)

@Preview(showBackground = true)
@Composable
private fun CategoryCreatePreview() = CategoriesPreview(previewEditor.copy(categoryId = null))

@Preview(showBackground = true)
@Composable
private fun CategoryEditPreview() = CategoriesPreview(previewEditor)

@Preview(showBackground = true)
@Composable
private fun DeleteUnusedPreview() = CategoriesPreview(CategorySurface.Delete(previewEditor, 0))

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeleteUsedPreview() = CategoriesPreview(CategorySurface.Delete(previewEditor, 3, SystemCategoryIds.OTHER), dark = true)

@Composable
private fun CategoriesPreview(
    surface: CategorySurface,
    dark: Boolean = false,
) {
    WherisTheme(darkTheme = dark) {
        CategoriesScreen(
            state = CategoriesUiState(previewCategories, isLoading = false, surface = surface),
            onCreate = {}, onEdit = {}, onNameChange = {}, onIconChange = {}, onColorChange = {},
            onSave = {}, onRequestDelete = {}, onReplacement = {}, onConfirmDelete = {}, onBack = {}, onRetry = {},
        )
    }
}
