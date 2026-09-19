package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PinRepositoryImplTest {
    private lateinit var repository: PinRepositoryImpl
    private lateinit var fakeDao: FakePinDao

    @Before
    fun setup() {
        fakeDao = FakePinDao()
        repository = PinRepositoryImpl(fakeDao)
    }

    @Test
    fun `savePin should call insertPin in Dao`() =
        runBlocking {
            val pin =
                Pin(
                    id = PinId("id1"),
                    position = GeoPoint(1.0, 2.0),
                    categoryId = CategoryId("cat1"),
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 100L,
                    updatedAtEpochMillis = 100L,
                )

            repository.savePin(pin)

            val saved = fakeDao.entities.first()[0]
            assertEquals("id1", saved.id)
            assertEquals(1.0, saved.latitude, 0.0)
        }

    @Test
    fun `observePins should map entities to domain`() =
        runBlocking {
            val entity =
                PinEntity(
                    id = "id1",
                    latitude = 1.0,
                    longitude = 2.0,
                    categoryId = "cat1",
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 100L,
                    updatedAtEpochMillis = 100L,
                )
            fakeDao.emit(listOf(entity))

            val observed = repository.observePins().first()
            assertEquals(1, observed.size)
            assertEquals(PinId("id1"), observed[0].id)
        }

    private class FakePinDao : PinDao {
        val entities = MutableStateFlow<List<PinEntity>>(emptyList())

        fun emit(list: List<PinEntity>) {
            entities.value = list
        }

        override fun observePins(): Flow<List<PinEntity>> = entities

        override fun observePin(pinId: String): Flow<PinEntity?> {
            return MutableStateFlow(entities.value.find { it.id == pinId })
        }

        override suspend fun insertPin(pin: PinEntity) {
            entities.value = entities.value + pin
        }
    }
}
