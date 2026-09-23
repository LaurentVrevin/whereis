package com.laurentvrevin.wheris.domain.repository

import com.laurentvrevin.wheris.domain.location.LocationResult

interface UserLocationRepository {
    /**
     * Attempts to acquire current foreground location within [timeoutMillis].
     */
    suspend fun getCurrentLocation(timeoutMillis: Long = 10_000L): LocationResult
}
