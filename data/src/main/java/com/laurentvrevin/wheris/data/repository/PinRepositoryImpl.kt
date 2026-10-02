package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.dao.PinMutationStatus
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.data.mapper.toEntity
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class PinRepositoryImpl(
    private val pinDao: PinDao,
    private val photoStorage: PhotoStorage,
) : PinRepository {
    private val mutationMutex = Mutex()
    private var recovered = false

    private suspend fun recover() {
        photoStorage.reconcile(pinDao.observePins().first().mapNotNull { it.photoReference?.let(::PhotoReference) }.toSet())
        recovered = true
    }

    private suspend fun ensureRecovered() =
        mutationMutex.withLock {
            if (!recovered) recover()
        }

    override fun observePins(): Flow<List<Pin>> {
        return pinDao.observePins().onStart { ensureRecovered() }.map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observePin(pinId: PinId): Flow<Pin?> {
        return pinDao.observePin(pinId.value).onStart { ensureRecovered() }.map { it?.toDomain() }
    }

    override suspend fun savePin(pin: Pin) {
        mutationMutex.withLock {
            if (!recovered) recover()
            pin.photoReference?.let { photo ->
                check(pinDao.observePins().first().none { it.photoReference == photo.value }) { "Photo already attached" }
                photoStorage.verifyPermanent(photo)
            }
            // Once insertion starts, finish the ownership handoff even if navigation cancels its caller.
            withContext(NonCancellable) {
                pinDao.insertPin(pin.toEntity())
                pin.photoReference?.let { photoStorage.attached(it) }
            }
        }
    }

    override suspend fun deletePin(pinId: PinId) {
        mutationMutex.withLock {
            withContext(NonCancellable) {
                // Also completes cleanup when this ID was already removed by an earlier attempt.
                recover()
                val photo =
                    pinDao.observePin(pinId.value).first()?.photoReference?.let(::PhotoReference)
                        ?.takeUnless { reference ->
                            // Preserve any unexpected legacy/shared reference; new sharing is rejected on insertion.
                            pinDao.observePins().first().any { it.id != pinId.value && it.photoReference == reference.value }
                        }
                photo?.let { photoStorage.stageDeletion(it) }
                try {
                    pinDao.deletePin(pinId.value)
                } catch (exception: Exception) {
                    try {
                        photo?.let { photoStorage.restoreDeletion(it) }
                    } catch (restoreFailure: Exception) {
                        exception.addSuppressed(restoreFailure)
                    }
                    throw exception
                }
                // Failure is propagated; the durable pending file is retried/reconciled later.
                photo?.let { photoStorage.finishDeletion(it) }
            }
        }
    }

    override suspend fun updatePin(
        update: PinUpdate,
        updatedAtEpochMillis: Long,
    ): PinUpdateResult {
        val result =
            mutationMutex.withLock {
                try {
                    // Invalid targets and KEEP must not trigger even a filesystem reconciliation.
                    val current = pinDao.getPin(update.pinId.value) ?: return@withLock PinUpdateResult.PIN_NOT_FOUND
                    if (!pinDao.categoryExists(update.categoryId.value)) return@withLock PinUpdateResult.CATEGORY_NOT_FOUND
                    val oldPhoto = current.photoReference?.let(::PhotoReference)
                    val nextPhoto =
                        when (val change = update.photoChange) {
                            PinPhotoChange.Keep -> oldPhoto
                            PinPhotoChange.Remove -> null
                            is PinPhotoChange.Replace -> {
                                // Replacing with the existing reference is logical KEEP, with no file access.
                                if (change.photo != oldPhoto) {
                                    if (pinDao.photoAttachedElsewhere(change.photo.value, current.id)) {
                                        return@withLock PinUpdateResult.PHOTO_ALREADY_ATTACHED
                                    }
                                    photoStorage.verifyPermanent(change.photo)
                                }
                                change.photo
                            }
                        }
                    val retiring =
                        oldPhoto?.takeIf {
                            it != nextPhoto && !pinDao.photoAttachedElsewhere(it.value, current.id)
                        }
                    currentCoroutineContext().ensureActive()
                    // Once staging starts, finish DB/restore/ownership cleanup despite caller cancellation.
                    withContext(NonCancellable) {
                        val status =
                            try {
                                retiring?.let { photoStorage.stageDeletion(it) }
                                pinDao.mutatePin(
                                    current.id,
                                    update.categoryId.value,
                                    update.name,
                                    update.note,
                                    update.isFavorite,
                                    nextPhoto?.value,
                                    current.photoReference,
                                    updatedAtEpochMillis,
                                )
                            } catch (exception: Exception) {
                                restoreRetiring(retiring, exception)
                                throw exception
                            }
                        if (status != PinMutationStatus.SUCCESS) {
                            retiring?.let { photoStorage.restoreDeletion(it) }
                            when (status) {
                                PinMutationStatus.PIN_NOT_FOUND -> PinUpdateResult.PIN_NOT_FOUND
                                PinMutationStatus.CATEGORY_NOT_FOUND -> PinUpdateResult.CATEGORY_NOT_FOUND
                                PinMutationStatus.PHOTO_ALREADY_ATTACHED -> PinUpdateResult.PHOTO_ALREADY_ATTACHED
                                PinMutationStatus.CONFLICT -> PinUpdateResult.TECHNICAL_FAILURE
                                PinMutationStatus.SUCCESS -> error("Handled above")
                            }
                        } else {
                            // A cleanup error after this point must never be reported as an unsaved mutation.
                            try {
                                if (nextPhoto != oldPhoto) nextPhoto?.let { photoStorage.attached(it) }
                                retiring?.let { photoStorage.finishDeletion(it) }
                                PinUpdateResult.SUCCESS
                            } catch (exception: CancellationException) {
                                recovered = false
                                throw exception
                            } catch (_: Exception) {
                                recovered = false
                                PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING
                            }
                        }
                    }
                } catch (exception: CancellationException) {
                    recovered = false
                    throw exception
                } catch (_: Exception) {
                    recovered = false
                    PinUpdateResult.TECHNICAL_FAILURE
                }
            }
        currentCoroutineContext().ensureActive()
        return result
    }

    private suspend fun restoreRetiring(
        photo: PhotoReference?,
        originalFailure: Exception,
    ) {
        try {
            photo?.let { photoStorage.restoreDeletion(it) }
        } catch (restoreFailure: Exception) {
            originalFailure.addSuppressed(restoreFailure)
            if (restoreFailure is CancellationException) throw restoreFailure
        }
    }
}
