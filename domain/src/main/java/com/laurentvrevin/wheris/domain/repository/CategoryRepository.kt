package com.laurentvrevin.wheris.domain.repository

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>

    suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category

    fun observeSystemCategories(): Flow<List<Category>>

    /** Updates only custom presentation. ID, creation time and places remain unchanged.
     * @throws IllegalArgumentException if the trimmed name is blank, as for creation.
     */
    suspend fun updateCustomCategory(
        categoryId: CategoryId,
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): CategoryMutationResult

    /** Counts references in storage; returns zero for an absent category. Storage failures propagate. */
    suspend fun getCategoryUsageCount(categoryId: CategoryId): Int

    /** Deletes only an existing unused custom category; never deletes places. */
    suspend fun deleteCustomCategory(categoryId: CategoryId): CategoryMutationResult

    /** Atomically reassigns all source places and deletes the custom source.
     * Reassigned places share one new modification timestamp; all other fields are preserved.
     */
    suspend fun reassignAndDeleteCustomCategory(
        sourceCategoryId: CategoryId,
        replacementCategoryId: CategoryId,
    ): CategoryMutationResult
}
