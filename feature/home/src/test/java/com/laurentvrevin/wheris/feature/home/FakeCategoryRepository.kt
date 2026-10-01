package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeCategoryRepository : CategoryRepository {
    val categories = MutableStateFlow<List<Category>>(emptyList())

    override fun observeCategories(): Flow<List<Category>> = categories

    override suspend fun updateCustomCategory(
        categoryId: com.laurentvrevin.wheris.core.model.CategoryId,
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

    override suspend fun getCategoryUsageCount(categoryId: com.laurentvrevin.wheris.core.model.CategoryId): Int = error("Not used")

    override suspend fun deleteCustomCategory(
        categoryId: com.laurentvrevin.wheris.core.model.CategoryId,
    ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

    override suspend fun reassignAndDeleteCustomCategory(
        sourceCategoryId: com.laurentvrevin.wheris.core.model.CategoryId,
        replacementCategoryId: com.laurentvrevin.wheris.core.model.CategoryId,
    ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

    override fun observeSystemCategories(): Flow<List<Category>> = error("Use all categories")

    override suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category = error("Creation is not used by this feature")
}
