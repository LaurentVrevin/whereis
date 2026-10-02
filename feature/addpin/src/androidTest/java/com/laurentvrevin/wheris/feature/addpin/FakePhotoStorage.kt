package com.laurentvrevin.wheris.feature.addpin

import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.domain.PhotoStorage
import kotlinx.coroutines.CompletableDeferred

internal class FakePhotoStorage : PhotoStorage {
    val promoted = mutableSetOf<PhotoReference>()
    val discarded = mutableListOf<PhotoDraftReference>()
    var promotionCalls = 0
    var promotionCompletion: CompletableDeferred<Unit>? = null
    var promotionFails = false
    var discardFails = false

    override suspend fun promote(draft: PhotoDraftReference): PhotoReference {
        promotionCompletion?.await()
        if (promotionFails) error("promotion failed")
        return PhotoReference(draft.value).also {
            promoted.add(it)
            promotionCalls++
        }
    }

    override suspend fun discard(draft: PhotoDraftReference) {
        if (discardFails) error("discard failed")
        discarded.add(draft)
    }

    override suspend fun verifyPermanent(photo: PhotoReference) {}

    override suspend fun attached(photo: PhotoReference) {}

    override suspend fun stageDeletion(photo: PhotoReference) {}

    override suspend fun restoreDeletion(photo: PhotoReference) {}

    override suspend fun finishDeletion(photo: PhotoReference) {}

    override suspend fun reconcile(attached: Set<PhotoReference>) {}
}
