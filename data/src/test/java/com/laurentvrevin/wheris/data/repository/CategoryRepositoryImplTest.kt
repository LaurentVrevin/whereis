package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.database.dao.CategoryMutationStatus
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryRepositoryImplTest {
    @Test
    fun updateNormalizesPresentationAndPreservesIdentityAndCreationTime() =
        runBlocking {
            val dao = FakeCategoryDao()
            val repository = CategoryRepositoryImpl(dao, clock = { 42L })
            val category = repository.createCustomCategory("Initial", CategoryIconKey.PLACE, CategoryColorKey.ORANGE)
            assertEquals(
                CategoryMutationResult.SUCCESS,
                repository.updateCustomCategory(category.id, "  散歩  ", CategoryIconKey.PARK, CategoryColorKey.PURPLE),
            )
            val updated = category.copy(name = "散歩", iconKey = CategoryIconKey.PARK, colorKey = CategoryColorKey.PURPLE)
            assertEquals(updated, repository.observeCategories().first().single())
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCustomCategory(category.id, " \t\n", CategoryIconKey.PLACE, CategoryColorKey.ORANGE) }
            }
            assertEquals(updated, repository.observeCategories().first().single())
        }

    @Test
    fun updateReportsAbsentAndProtectedCategories() =
        runBlocking {
            val dao = FakeCategoryDao()
            val system = CategoryEntity(SystemCategoryIds.OTHER.value, true)
            dao.categories.value = listOf(system)
            val repository = CategoryRepositoryImpl(dao)
            assertEquals(
                CategoryMutationResult.NOT_FOUND,
                repository.updateCustomCategory(CategoryId("missing"), "Name", CategoryIconKey.PARK, CategoryColorKey.GREEN),
            )
            assertEquals(
                CategoryMutationResult.SYSTEM_PROTECTED,
                repository.updateCustomCategory(SystemCategoryIds.OTHER, "Name", CategoryIconKey.PARK, CategoryColorKey.GREEN),
            )
            assertEquals(listOf(system), dao.categories.value)
        }

    @Test
    fun technicalFailureIsExplicitAndCancellationStillPropagates() =
        runBlocking {
            val dao = FakeCategoryDao()
            val repository = CategoryRepositoryImpl(dao)
            dao.deletionFailure = java.io.IOException("Synthetic storage failure")
            assertEquals(CategoryMutationResult.TECHNICAL_FAILURE, repository.deleteCustomCategory(CategoryId("source")))
            dao.deletionFailure = CancellationException("Cancelled")
            assertThrows(CancellationException::class.java) {
                runBlocking { repository.deleteCustomCategory(CategoryId("source")) }
            }
            Unit
        }

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

    @Test
    fun customCreationNormalizesPersistsAndIsObservableWithStableSystemOrder() =
        runBlocking {
            val dao = FakeCategoryDao()
            dao.categories.value =
                listOf(
                    CategoryEntity(SystemCategoryIds.OTHER.value, true),
                    CategoryEntity(SystemCategoryIds.CAR.value, true),
                )
            val repository = CategoryRepositoryImpl(dao, clock = { 42_000L })
            val first = repository.createCustomCategory("  Camping  ", CategoryIconKey.PARK, CategoryColorKey.GREEN)
            val second = repository.createCustomCategory("Camping", CategoryIconKey.PHOTO_CAMERA, CategoryColorKey.BLUE)
            assertEquals("Camping", first.name)
            assertNotEquals(first.id, second.id)
            assertTrue(first.id !in SystemCategoryIds.ALL)
            assertEquals(42_000L, first.createdAtEpochMillis)
            assertEquals(CategoryIconKey.PARK, first.iconKey)
            assertEquals(CategoryColorKey.GREEN, first.colorKey)
            assertEquals(first, requireNotNull(dao.getCategoryById(first.id.value)).toDomain())
            val expectedCustom = listOf(first, second).sortedWith(compareBy<Category> { it.createdAtEpochMillis }.thenBy { it.id.value })
            assertEquals(
                listOf(SystemCategoryIds.CAR, SystemCategoryIds.OTHER) + expectedCustom.map { it.id },
                repository.observeCategories().first().map { it.id },
            )
            assertEquals(
                listOf(SystemCategoryIds.CAR, SystemCategoryIds.OTHER),
                repository.observeSystemCategories().first().map { it.id },
            )
        }

    @Test
    fun blankCustomNamesAreRejectedWithoutWriting() =
        runBlocking {
            val dao = FakeCategoryDao()
            val repository = CategoryRepositoryImpl(dao)
            listOf("", "  ", "\t\n").forEach { name ->
                assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { repository.createCustomCategory(name, CategoryIconKey.PLACE, CategoryColorKey.ORANGE) }
                }
            }
            assertTrue(dao.categories.value.isEmpty())
        }

    private class FakeCategoryDao : CategoryDao() {
        val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
        var deletionFailure: Exception? = null

        override suspend fun deleteCustomCategory(id: String): CategoryMutationStatus {
            deletionFailure?.let { throw it }
            return super.deleteCustomCategory(id)
        }

        override suspend fun insertCategory(category: CategoryEntity) {
            categories.value = categories.value + category
        }

        override suspend fun getCategoryById(id: String): CategoryEntity? = categories.value.find { it.id == id }

        override fun observeCategories(): Flow<List<CategoryEntity>> = categories

        override fun observeSystemCategories(): Flow<List<CategoryEntity>> = categories.map { rows -> rows.filter { it.isSystem } }

        override suspend fun getCategoryUsageCount(categoryId: String): Int = 0

        override suspend fun updateCustomRow(
            id: String,
            name: String,
            iconKey: String,
            colorKey: String,
        ): Int {
            val source = categories.value.firstOrNull { it.id == id && !it.isSystem } ?: return 0
            categories.value =
                categories.value.map {
                    if (it.id == id) source.copy(name = name, iconKey = iconKey, colorKey = colorKey) else it
                }
            return 1
        }

        override suspend fun deleteCustomRow(id: String): Int = error("Not used")

        override suspend fun reassignPins(
            sourceId: String,
            replacementId: String,
            updatedAt: Long,
        ): Int = error("Not used")
    }
}
