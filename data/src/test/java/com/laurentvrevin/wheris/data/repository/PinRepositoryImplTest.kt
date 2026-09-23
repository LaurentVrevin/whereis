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
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PinRepositoryImplTest {
    private lateinit var repository: PinRepositoryImpl
    private lateinit var fakeDao: FakePinDao

    @Before
    fun setup() {
        fakeDao = FakePinDao()
        repository = PinRepositoryImpl(fakeDao)
    }

    @Test
    fun `savePin should map all fields correctly to entity`() =
        runBlocking {
            val pin =
                Pin(
                    id = PinId("id1"),
                    position = GeoPoint(1.23, 4.56),
                    categoryId = CategoryId("cat_test"),
                    accuracyMeters = 5.0f,
                    altitudeMeters = 100.0,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 2000L,
                )

            repository.savePin(pin)

            val saved = fakeDao.entities.value[0]
            assertEquals("id1", saved.id)
            assertEquals(1.23, saved.latitude, 0.0)
            assertEquals(4.56, saved.longitude, 0.0)
            assertEquals("cat_test", saved.categoryId)
            assertEquals(5.0f, saved.accuracyMeters)
            assertEquals(100.0, saved.altitudeMeters)
            assertEquals(1000L, saved.createdAtEpochMillis)
            assertEquals(2000L, saved.updatedAtEpochMillis)
        }

    @Test
    fun `observePins should map all entity fields correctly to domain`() =
        runBlocking {
            val entity =
                PinEntity(
                    id = "id1",
                    latitude = 1.23,
                    longitude = 4.56,
                    categoryId = "cat_test",
                    accuracyMeters = 5.0f,
                    altitudeMeters = 100.0,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 2000L,
                )
            fakeDao.emit(listOf(entity))

            val observed = repository.observePins().first()[0]
            assertEquals(PinId("id1"), observed.id)
            assertEquals(1.23, observed.position.latitude, 0.0)
            assertEquals(4.56, observed.position.longitude, 0.0)
            assertEquals(CategoryId("cat_test"), observed.categoryId)
            assertEquals(5.0f, observed.accuracyMeters)
            assertEquals(100.0, observed.altitudeMeters)
            assertEquals(1000L, observed.createdAtEpochMillis)
            assertEquals(2000L, observed.updatedAtEpochMillis)
        }

    @Test
    fun `observePin with existing ID should return mapped pin`() =
        runBlocking {
            val entity =
                PinEntity(
                    id = "find_me",
                    latitude = 1.0,
                    longitude = 1.0,
                    categoryId = "cat1",
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 100L,
                    updatedAtEpochMillis = 100L,
                )
            fakeDao.emit(listOf(entity))

            val observed = repository.observePin(PinId("find_me")).first()
            assertEquals(PinId("find_me"), observed?.id)
        }

    @Test
    fun `observePin with missing ID should return null`() =
        runBlocking {
            val observed = repository.observePin(PinId("missing")).first()
            assertNull(observed)
        }

    @Test
    fun `savePin should propagate exceptions from Dao`() {
        val pin =
            Pin(
                id = PinId("id1"),
                position = GeoPoint(0.0, 0.0),
                categoryId = CategoryId("cat1"),
                accuracyMeters = null,
                altitudeMeters = null,
                createdAtEpochMillis = 0L,
                updatedAtEpochMillis = 0L,
            )
        fakeDao.shouldFail = true

        assertThrows(IOException::class.java) {
            runBlocking { repository.savePin(pin) }
        }
    }

    private class FakePinDao : PinDao {
        val entities = MutableStateFlow<List<PinEntity>>(emptyList())
        var shouldFail = false

        fun emit(list: List<PinEntity>) {
            entities.value = list
        }

        override fun observePins(): Flow<List<PinEntity>> = entities

        override fun observePin(pinId: String): Flow<PinEntity?> {
            return MutableStateFlow(entities.value.find { it.id == pinId })
        }

        override suspend fun insertPin(pin: PinEntity) {
            if (shouldFail) throw IOException("Fake DAO error")
            entities.value = entities.value + pin
        }
    }
}
