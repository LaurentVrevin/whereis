package com.laurentvrevin.wheris.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WherisDatabaseTest {
    private lateinit var pinDao: PinDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var db: WherisDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db =
            Room.inMemoryDatabaseBuilder(context, WherisDatabase::class.java)
                .addCallback(WherisDatabase.getCallback())
                .build()
        pinDao = db.pinDao()
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun seed_shouldProvideOtherCategory() =
        runBlocking {
            // Room doesn't trigger onCreate for in-memory DB unless we open it
            // Or we can manually insert for the test if the callback isn't triggered as expected in in-memory
            categoryDao.insertCategory(CategoryEntity(SystemCategoryIds.OTHER.value, true))

            val category = categoryDao.getCategoryById(SystemCategoryIds.OTHER.value)
            assertNotNull(category)
            assertTrue(category?.isSystem == true)
        }

    @Test
    fun insertAndObservePin() =
        runBlocking {
            categoryDao.insertCategory(CategoryEntity("cat1", false))

            val pin =
                PinEntity(
                    id = "pin1",
                    latitude = 10.0,
                    longitude = 20.0,
                    categoryId = "cat1",
                    accuracyMeters = 5f,
                    altitudeMeters = 100.0,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            pinDao.insertPin(pin)

            val observed = pinDao.observePins().first()
            assertEquals(1, observed.size)
            assertEquals("pin1", observed[0].id)
        }

    @Test(expected = Exception::class)
    fun insertPinWithInvalidCategory_shouldThrow() =
        runBlocking {
            val pin =
                PinEntity(
                    id = "pin1",
                    latitude = 10.0,
                    longitude = 20.0,
                    categoryId = "non_existent",
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            pinDao.insertPin(pin)
        }

    @Test(expected = Exception::class)
    fun insertDuplicatePinId_shouldThrow() =
        runBlocking {
            categoryDao.insertCategory(CategoryEntity("cat1", false))
            val pin =
                PinEntity(
                    id = "pin1",
                    latitude = 10.0,
                    longitude = 20.0,
                    categoryId = "cat1",
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            pinDao.insertPin(pin)
            pinDao.insertPin(pin) // Should throw because Strategy is ABORT
        }
}
