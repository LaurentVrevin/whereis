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
     *
     * @throws Exception if a pin with the same ID already exists.
     */
    suspend fun savePin(pin: Pin)

    /**
     * Deletes only this place. An already absent place is treated as deleted.
     * Before real photo acquisition is introduced, data must coordinate owned photo cleanup
     * here, with recoverable failures; Room cannot make file deletion atomic.
     * See docs/engineering/LOCAL_PHOTO_LIFECYCLE.md.
     */
    suspend fun deletePin(pinId: PinId)

    /** Updates only editable text and modification time; false if the place no longer exists. */
    suspend fun updatePinDetails(
        pinId: PinId,
        name: String?,
        note: String?,
        updatedAtEpochMillis: Long,
    ): Boolean
}
