package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val clock: () -> Long = System::currentTimeMillis,
) : CategoryRepository {
    private val systemOrder = SystemCategoryIds.ALL.withIndex().associate { (index, id) -> id to index }
    private val categoryOrder =
        compareBy<Category> { if (it.isSystem) 0 else 1 }
            .thenBy { systemOrder[it.id] ?: Int.MAX_VALUE }
            .thenBy { it.createdAtEpochMillis }
            .thenBy { it.id.value }

    override fun observeSystemCategories(): Flow<List<Category>> =
        categoryDao.observeSystemCategories().map { entities -> entities.map { it.toDomain() }.sortedWith(categoryOrder) }

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeCategories().map { entities -> entities.map { it.toDomain() }.sortedWith(categoryOrder) }

    override suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category {
        val category =
            Category(
                id = CategoryId(UUID.randomUUID().toString()),
                isSystem = false,
                name = name.trim(),
                iconKey = iconKey,
                colorKey = colorKey,
                createdAtEpochMillis = clock(),
            )
        categoryDao.insertCategory(
            CategoryEntity(
                id = category.id.value,
                isSystem = false,
                name = category.name,
                iconKey = iconKey.value,
                colorKey = colorKey.value,
                createdAtEpochMillis = category.createdAtEpochMillis,
            ),
        )
        return category
    }
}
