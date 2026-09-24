package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeSystemCategories(): Flow<List<Category>> {
        val order = SystemCategoryIds.ALL.withIndex().associate { (index, id) -> id to index }
        return categoryDao.observeSystemCategories().map { entities ->
            entities
                .map { it.toDomain() }
                .sortedBy { order[it.id] ?: Int.MAX_VALUE }
        }
    }
}
