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

    override fun observeSystemCategories(): Flow<List<Category>> = error("Use all categories")

    override suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category = error("Creation is not used by this feature")
}
