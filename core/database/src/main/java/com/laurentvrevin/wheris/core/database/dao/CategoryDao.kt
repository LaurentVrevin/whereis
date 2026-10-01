package com.laurentvrevin.wheris.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

// Mirrors SystemCategoryIds.ALL; custom rows follow in creation order with an ID tie-breaker.
private const val SYSTEM_CATEGORY_ORDER =
    "CASE id WHEN 'car' THEN 0 WHEN 'tent' THEN 1 WHEN 'bivouac' THEN 2 " +
        "WHEN 'restaurant' THEN 3 WHEN 'photo_spot' THEN 4 WHEN 'viewpoint' THEN 5 " +
        "WHEN 'bike' THEN 6 WHEN 'parking' THEN 7 WHEN 'beach' THEN 8 WHEN 'fishing' THEN 9 " +
        "WHEN 'hiking' THEN 10 WHEN 'meeting' THEN 11 WHEN 'other' THEN 12 ELSE 13 END"

@Dao
abstract class CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertCategory(category: CategoryEntity)

    @Query(
        "SELECT * FROM categories ORDER BY isSystem DESC, " +
            SYSTEM_CATEGORY_ORDER + ", createdAtEpochMillis ASC, id ASC",
    )
    abstract fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    abstract suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE isSystem = 1 ORDER BY " + SYSTEM_CATEGORY_ORDER + ", id ASC")
    abstract fun observeSystemCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM pins WHERE categoryId = :categoryId")
    abstract suspend fun getCategoryUsageCount(categoryId: String): Int

    @Query("UPDATE categories SET name = :name, iconKey = :iconKey, colorKey = :colorKey WHERE id = :id AND isSystem = 0")
    protected abstract suspend fun updateCustomRow(
        id: String,
        name: String,
        iconKey: String,
        colorKey: String,
    ): Int

    @Query("DELETE FROM categories WHERE id = :id AND isSystem = 0")
    protected abstract suspend fun deleteCustomRow(id: String): Int

    @Query("UPDATE pins SET categoryId = :replacementId, updatedAtEpochMillis = :updatedAt WHERE categoryId = :sourceId")
    protected abstract suspend fun reassignPins(
        sourceId: String,
        replacementId: String,
        updatedAt: Long,
    ): Int

    @Transaction
    open suspend fun updateCustomCategory(
        id: String,
        name: String,
        iconKey: String,
        colorKey: String,
    ): CategoryMutationStatus {
        val source = getCategoryById(id) ?: return CategoryMutationStatus.NOT_FOUND
        if (source.isSystem) return CategoryMutationStatus.SYSTEM_PROTECTED
        // Reuse entity validation for direct DAO writes, including supported keys and normalized names.
        source.copy(name = name, iconKey = iconKey, colorKey = colorKey)
        check(updateCustomRow(id, name, iconKey, colorKey) == 1)
        return CategoryMutationStatus.SUCCESS
    }

    @Transaction
    open suspend fun deleteCustomCategory(id: String): CategoryMutationStatus {
        val source = getCategoryById(id) ?: return CategoryMutationStatus.NOT_FOUND
        if (source.isSystem) return CategoryMutationStatus.SYSTEM_PROTECTED
        if (getCategoryUsageCount(id) != 0) return CategoryMutationStatus.IN_USE
        check(deleteCustomRow(id) == 1)
        return CategoryMutationStatus.SUCCESS
    }

    @Transaction
    open suspend fun reassignAndDeleteCustomCategory(
        sourceId: String,
        replacementId: String,
        updatedAt: Long,
    ): CategoryMutationStatus {
        val source = getCategoryById(sourceId) ?: return CategoryMutationStatus.NOT_FOUND
        if (source.isSystem) return CategoryMutationStatus.SYSTEM_PROTECTED
        if (sourceId == replacementId) return CategoryMutationStatus.SAME_CATEGORY
        if (getCategoryById(replacementId) == null) return CategoryMutationStatus.REPLACEMENT_NOT_FOUND
        val expectedCount = getCategoryUsageCount(sourceId)
        check(reassignPins(sourceId, replacementId, updatedAt) == expectedCount)
        check(getCategoryUsageCount(sourceId) == 0)
        check(deleteCustomRow(sourceId) == 1)
        return CategoryMutationStatus.SUCCESS
    }
}
