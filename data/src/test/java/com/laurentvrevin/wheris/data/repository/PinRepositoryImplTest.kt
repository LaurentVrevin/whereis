package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
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
                    name = "Camping",
                    note = "Près du chemin",
                    isFavorite = true,
                    photoReference = PhotoReference("photo-42"),
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
            assertEquals("Camping", saved.name)
            assertEquals("Près du chemin", saved.note)
            assertEquals(true, saved.isFavorite)
            assertEquals("photo-42", saved.photoReference)
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
                    name = "Camping",
                    note = "Près du chemin",
                    isFavorite = true,
                    photoReference = "photo-42",
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
            assertEquals("Camping", observed.name)
            assertEquals("Près du chemin", observed.note)
            assertEquals(true, observed.isFavorite)
            assertEquals(PhotoReference("photo-42"), observed.photoReference)
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
                    isFavorite = true,
                    photoReference = "photo-found",
                )
            fakeDao.emit(listOf(entity))

            val observed = repository.observePin(PinId("find_me")).first()
            assertEquals(PinId("find_me"), observed?.id)
            assertEquals(true, observed?.isFavorite)
            assertEquals(PhotoReference("photo-found"), observed?.photoReference)
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

    @Test
    fun `deletePin forwards only the requested identifier`() =
        runBlocking {
            val entity =
                PinEntity(
                    id = "delete-me",
                    latitude = 1.0,
                    longitude = 2.0,
                    categoryId = "category",
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 100L,
                    updatedAtEpochMillis = 100L,
                )
            val other = entity.copy(id = "keep-me")
            fakeDao.emit(listOf(entity, other))

            repository.deletePin(PinId(entity.id))

            assertEquals(listOf(other), fakeDao.entities.value)
        }

    @Test
    fun `deletePin propagates storage failure`() {
        fakeDao.shouldFail = true

        assertThrows(IOException::class.java) {
            runBlocking { repository.deletePin(PinId("delete-me")) }
        }
    }

    @Test
    fun `updating details preserves other fields and reports a missing place`() =
        runBlocking {
            val original =
                PinEntity("edit-me", 10.0, 20.0, "category", 5f, 30.0, 1000L, 2000L, isFavorite = true, photoReference = "photo-edit")
            fakeDao.emit(listOf(original))

            assertEquals(true, repository.updatePinDetails(PinId("edit-me"), "Name", "Note", 3000L))
            assertEquals(
                listOf(original.copy(name = "Name", note = "Note", updatedAtEpochMillis = 3000L)),
                fakeDao.entities.value,
            )
            assertEquals(false, repository.updatePinDetails(PinId("missing"), "Absent", null, 4000L))
            assertEquals(1, fakeDao.entities.value.size)
        }

    @Test
    fun `updating details propagates storage errors`() {
        fakeDao.shouldFail = true
        assertThrows(IOException::class.java) {
            runBlocking { repository.updatePinDetails(PinId("edit-me"), "Name", "Note", 3000L) }
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

        override suspend fun deletePin(pinId: String) {
            if (shouldFail) throw IOException("Fake DAO error")
            entities.value = entities.value.filterNot { it.id == pinId }
        }

        override suspend fun updateDetails(
            pinId: String,
            name: String?,
            note: String?,
            updatedAt: Long,
        ): Int {
            if (shouldFail) throw IOException("Fake DAO error")
            if (entities.value.none { it.id == pinId }) return 0
            entities.value =
                entities.value.map {
                    if (it.id == pinId) it.copy(name = name, note = note, updatedAtEpochMillis = updatedAt) else it
                }
            return 1
        }
    }
}
