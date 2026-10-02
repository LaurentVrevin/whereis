package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.data.mapper.toEntity
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.PinRepository
import kotlinx.coroutines.NonCancellable
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

    override suspend fun updatePinDetails(
        pinId: PinId,
        name: String?,
        note: String?,
        updatedAtEpochMillis: Long,
    ): Boolean = pinDao.updateDetails(pinId.value, name, note, updatedAtEpochMillis) == 1
}
