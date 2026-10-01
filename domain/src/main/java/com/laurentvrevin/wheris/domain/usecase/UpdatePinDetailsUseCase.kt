package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinRepository

class UpdatePinDetailsUseCase(
    private val pinRepository: PinRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(pinId: PinId, name: String, note: String): Boolean =
        pinRepository.updatePinDetails(
            pinId = pinId,
            name = name.trim().takeIf { it.isNotEmpty() },
            note = note.takeIf { it.isNotBlank() },
            updatedAtEpochMillis = clock(),
        )
}
