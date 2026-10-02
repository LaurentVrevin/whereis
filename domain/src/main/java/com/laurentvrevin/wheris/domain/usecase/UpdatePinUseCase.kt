package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult

class UpdatePinUseCase(
    private val pinRepository: PinRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(update: PinUpdate): PinUpdateResult =
        pinRepository.updatePin(
            update.copy(
                name = update.name?.trim()?.takeIf { it.isNotEmpty() },
                note = update.note?.takeIf { it.isNotBlank() },
            ),
            updatedAtEpochMillis = clock(),
        )
}
