package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository

class GetCurrentLocationUseCase(
    private val userLocationRepository: UserLocationRepository,
) {
    suspend operator fun invoke(timeoutMillis: Long = 10_000L): LocationResult {
        return userLocationRepository.getCurrentLocation(timeoutMillis)
    }
}
