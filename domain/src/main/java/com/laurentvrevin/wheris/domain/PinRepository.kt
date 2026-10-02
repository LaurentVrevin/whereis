package com.laurentvrevin.wheris.domain

import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Pin data management.
 */
interface PinRepository {
    /**
     * Observes the list of all saved pins.
     */
    fun observePins(): Flow<List<Pin>>

    /**
     * Observes a specific pin by its identifier.
     */
    fun observePin(pinId: PinId): Flow<Pin?>

    /**
     * Saves a new pin including optional details. A photo reference must already be permanent.
     * Its physical photo must not be shared with another Pin.
     *
     * @throws Exception if a pin with the same ID already exists.
     */
    suspend fun savePin(pin: Pin)

    /**
     * Deletes only this place. An already absent place is treated as deleted.
     * Coordinates owned photo cleanup through recoverable pending deletion. A database
     * failure preserves the photo; a final file failure remains durable for retry/recovery.
     * Room and the filesystem are not one atomic transaction.
     * See docs/engineering/LOCAL_PHOTO_LIFECYCLE.md.
     */
    suspend fun deletePin(pinId: PinId)

    /**
     * Canonical mutation of editable fields. SUCCESS_WITH_CLEANUP_PENDING also means committed.
     * Ordinary storage failures return a result; coroutine cancellation still propagates.
     * No file is touched for an absent Pin/category or KEEP. Replacement must be permanent.
     */
    suspend fun updatePin(
        update: PinUpdate,
        updatedAtEpochMillis: Long,
    ): PinUpdateResult
}
