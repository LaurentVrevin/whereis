package com.laurentvrevin.wheris.core.model

/**
 * Domain model for a saved place (Pin).
 * Minimal version for Step A2.
 */
data class Pin(
    val id: PinId,
    val position: GeoPoint,
    val categoryId: CategoryId,
    val accuracyMeters: Float?,
    val altitudeMeters: Double?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
