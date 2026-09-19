package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.data.mapper.toEntity
import com.laurentvrevin.wheris.domain.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PinRepositoryImpl(
    private val pinDao: PinDao,
) : PinRepository {
    override fun observePins(): Flow<List<Pin>> {
        return pinDao.observePins().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observePin(pinId: PinId): Flow<Pin?> {
        return pinDao.observePin(pinId.value).map { it?.toDomain() }
    }

    override suspend fun savePin(pin: Pin) {
        // Validation of existing ID is handled by DAO strategy (ABORT on conflict)
        // This will throw if ID already exists.
        pinDao.insertPin(pin.toEntity())
    }
}
