package com.laurentvrevin.wheris.domain

import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference

/** Local file lifecycle. Platform acquisition and physical paths stay behind this boundary. */
interface PhotoStorage {
    /** Idempotent for an active draft, including a promoted draft awaiting a database retry. */
    suspend fun promote(draft: PhotoDraftReference): PhotoReference

    suspend fun discard(draft: PhotoDraftReference)

    /** Releases an abandoned workflow's lease even if physical cleanup must be retried. */
    suspend fun abandon(draft: PhotoDraftReference) = discard(draft)

    /** Rejects an absent or known unreadable permanent file before database insertion. */
    suspend fun verifyPermanent(photo: PhotoReference)

    suspend fun attached(photo: PhotoReference)

    suspend fun stageDeletion(photo: PhotoReference)

    suspend fun restoreDeletion(photo: PhotoReference)

    suspend fun finishDeletion(photo: PhotoReference)

    /** Keeps database references and active in-process drafts; recovers interrupted deletions. */
    suspend fun reconcile(attached: Set<PhotoReference>)
}
