package com.laurentvrevin.wheris.feature.pindetail

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class EditCategories : CategoryRepository {
    val categories = MutableStateFlow(listOf(Category(SystemCategoryIds.PARKING, true), Category(SystemCategoryIds.OTHER, true)))
    var failure: Exception? = null

    override fun observeCategories(): Flow<List<Category>> = failure?.let { error -> flow { throw error } } ?: categories

    override fun observeSystemCategories() = observeCategories()

    override suspend fun createCustomCategory(
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): Category =
        error(
            "Not in scope",
        )

    override suspend fun updateCustomCategory(
        categoryId: CategoryId,
        name: String,
        iconKey: CategoryIconKey,
        colorKey: CategoryColorKey,
    ): CategoryMutationResult =
        error(
            "Not in scope",
        )

    override suspend fun getCategoryUsageCount(categoryId: CategoryId) = 0

    override suspend fun deleteCustomCategory(categoryId: CategoryId): CategoryMutationResult = error("Not in scope")

    override suspend fun reassignAndDeleteCustomCategory(
        sourceCategoryId: CategoryId,
        replacementCategoryId: CategoryId,
    ): CategoryMutationResult =
        error(
            "Not in scope",
        )
}

internal class EditPhotos : PhotoStorage {
    val discarded = mutableListOf<PhotoDraftReference>()
    var promotions = 0
    var promotionFailure: Exception? = null
    var discardFailure: Exception? = null
    var promotionCompletion: CompletableDeferred<Unit>? = null

    override suspend fun promote(draft: PhotoDraftReference): PhotoReference {
        promotions++
        promotionCompletion?.await()
        promotionFailure?.let { throw it }
        return PhotoReference(draft.value)
    }

    override suspend fun discard(draft: PhotoDraftReference) {
        discardFailure?.let { throw it }
        discarded += draft
    }

    override suspend fun verifyPermanent(photo: PhotoReference) = Unit

    override suspend fun attached(photo: PhotoReference) = Unit

    override suspend fun stageDeletion(photo: PhotoReference) = error("VM must never stage the original")

    override suspend fun restoreDeletion(photo: PhotoReference) = error("VM must never restore the original")

    override suspend fun finishDeletion(photo: PhotoReference) = error("VM must never delete the original")

    override suspend fun reconcile(attached: Set<PhotoReference>) = Unit
}
