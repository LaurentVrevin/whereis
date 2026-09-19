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
     * Saves a new pin.
     *
     * @throws Exception if a pin with the same ID already exists.
     */
    suspend fun savePin(pin: Pin)
}
