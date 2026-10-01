package com.laurentvrevin.wheris.domain.repository

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>

    suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category

    fun observeSystemCategories(): Flow<List<Category>>
}
