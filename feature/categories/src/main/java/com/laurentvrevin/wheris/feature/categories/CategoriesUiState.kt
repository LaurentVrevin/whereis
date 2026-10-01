package com.laurentvrevin.wheris.feature.categories

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val surface: CategorySurface = CategorySurface.List,
    val notice: CategoryMessage? = null,
)

sealed interface CategorySurface {
    data object List : CategorySurface

    data class Editor(
        val categoryId: CategoryId? = null,
        val name: String = "",
        val iconKey: CategoryIconKey = CategoryIconKey.PLACE,
        val colorKey: CategoryColorKey = CategoryColorKey.ORANGE,
        val isBusy: Boolean = false,
        val error: CategoryMessage? = null,
    ) : CategorySurface {
        val canSave: Boolean get() = name.isNotBlank() && !isBusy
    }

    data class Delete(
        val editor: Editor,
        val usageCount: Int,
        val replacementId: CategoryId? = null,
        val isBusy: Boolean = false,
        val error: CategoryMessage? = null,
    ) : CategorySurface
}

enum class CategoryMessage {
    LOAD_FAILED,
    SAVE_FAILED,
    USAGE_FAILED,
    DELETE_FAILED,
    NOT_FOUND,
    SYSTEM_PROTECTED,
    REPLACEMENT_NOT_FOUND,
    INVALID_REPLACEMENT,
}
