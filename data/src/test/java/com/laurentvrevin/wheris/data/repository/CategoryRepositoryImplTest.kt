package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryRepositoryImplTest {
    @Test
    fun `observeSystemCategories maps and orders known system categories`() =
        runBlocking {
            val dao = FakeCategoryDao()
            val repository = CategoryRepositoryImpl(dao)
            dao.categories.value =
                listOf(
                    CategoryEntity(SystemCategoryIds.OTHER.value, true),
                    CategoryEntity(SystemCategoryIds.CAR.value, true),
                    CategoryEntity(SystemCategoryIds.PARKING.value, true),
                )

            val categories = repository.observeSystemCategories().first()

            assertEquals(
                listOf(
                    SystemCategoryIds.CAR,
                    SystemCategoryIds.PARKING,
                    SystemCategoryIds.OTHER,
                ),
                categories.map { it.id },
            )
        }

    private class FakeCategoryDao : CategoryDao {
        val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())

        override suspend fun insertCategory(category: CategoryEntity) {
            categories.value = categories.value + category
        }

        override suspend fun getCategoryById(id: String): CategoryEntity? = categories.value.find { it.id == id }

        override fun observeSystemCategories(): Flow<List<CategoryEntity>> = categories
    }
}
