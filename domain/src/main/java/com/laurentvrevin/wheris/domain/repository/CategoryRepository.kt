package com.laurentvrevin.wheris.domain.repository

import com.laurentvrevin.wheris.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeSystemCategories(): Flow<List<Category>>
}
