package com.laurentvrevin.wheris.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

// Mirrors SystemCategoryIds.ALL; custom rows follow in creation order with an ID tie-breaker.
private const val SYSTEM_CATEGORY_ORDER =
    "CASE id WHEN 'car' THEN 0 WHEN 'tent' THEN 1 WHEN 'bivouac' THEN 2 " +
        "WHEN 'restaurant' THEN 3 WHEN 'photo_spot' THEN 4 WHEN 'viewpoint' THEN 5 " +
        "WHEN 'bike' THEN 6 WHEN 'parking' THEN 7 WHEN 'beach' THEN 8 WHEN 'fishing' THEN 9 " +
        "WHEN 'hiking' THEN 10 WHEN 'meeting' THEN 11 WHEN 'other' THEN 12 ELSE 13 END"

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity)

    @Query(
        "SELECT * FROM categories ORDER BY isSystem DESC, " +
            SYSTEM_CATEGORY_ORDER + ", createdAtEpochMillis ASC, id ASC",
    )
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE isSystem = 1 ORDER BY " + SYSTEM_CATEGORY_ORDER + ", id ASC")
    fun observeSystemCategories(): Flow<List<CategoryEntity>>
}
