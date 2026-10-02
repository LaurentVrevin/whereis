package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

class UpdatePinDetailsUseCase(
    private val pinRepository: PinRepository,
    clock: () -> Long = System::currentTimeMillis,
) {
    private val updatePin = UpdatePinUseCase(pinRepository, clock)

    /** Compatibility for the existing text editor; there is no separate persistence path. */
    suspend operator fun invoke(
        pinId: PinId,
        name: String,
        note: String,
    ): PinUpdateResult =
        try {
            val current = pinRepository.observePin(pinId).first()
            if (current == null) {
                PinUpdateResult.PIN_NOT_FOUND
            } else {
                updatePin(PinUpdate(pinId, current.categoryId, name, note, current.isFavorite, PinPhotoChange.Keep))
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            PinUpdateResult.TECHNICAL_FAILURE
        }
}
