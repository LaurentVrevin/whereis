package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.model.distanceTo

data class HomeUiState(
    val pins: List<Pin> = emptyList(),
    val selectedPinId: PinId? = null,
    val userLocation: UserLocation? = null,
    val dataUnavailable: Boolean = false,
) {
    val selectedPin: Pin?
        get() = pins.firstOrNull { it.id == selectedPinId }

    val distanceMeters: Double?
        get() {
            val pin = selectedPin
            val loc = userLocation
            return if (pin != null && loc != null) {
                loc.position.distanceTo(pin.position)
            } else {
                null
            }
        }
}
