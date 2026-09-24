package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import java.util.UUID

class CreatePinUseCase(
    private val pinRepository: PinRepository,
    private val idFactory: () -> PinId = { PinId(UUID.randomUUID().toString()) },
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(
        location: UserLocation,
        categoryId: CategoryId,
    ): Pin {
        val now = clock()
        val pin =
            Pin(
                id = idFactory(),
                position = location.position,
                categoryId = categoryId,
                accuracyMeters = location.accuracyMeters,
                altitudeMeters = location.altitudeMeters,
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            )

        pinRepository.savePin(pin)
        return pin
    }
}
