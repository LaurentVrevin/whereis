package com.laurentvrevin.wheris.domain.location

import com.laurentvrevin.wheris.core.model.UserLocation

/**
 * Result of a foreground location acquisition attempt.
 */
sealed interface LocationResult {
    data class Success(val location: UserLocation) : LocationResult

    data object ServicesDisabled : LocationResult

    data object Timeout : LocationResult

    data class Error(val cause: Throwable? = null) : LocationResult
}
