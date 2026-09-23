package com.laurentvrevin.wheris.domain.repository

import com.laurentvrevin.wheris.domain.location.LocationResult

interface UserLocationRepository {
    /**
     * Attempts to acquire current foreground location.
     */
    suspend fun getCurrentLocation(): LocationResult
}
