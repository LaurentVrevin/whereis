package com.laurentvrevin.wheris.core.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
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
import org.junit.Assert.assertThrows
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
    fun seed_shouldProvideOtherCategoryAutomatically() =
        runBlocking {
            // Proactive trigger to open the database and run the callback
            db.openHelper.writableDatabase

            val category = categoryDao.getCategoryById(SystemCategoryIds.OTHER.value)
            assertNotNull("System category OTHER should be seeded automatically", category)
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

    @Test
    fun insertPinWithInvalidCategory_shouldThrowConstraintException() =
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

            assertThrows(SQLiteConstraintException::class.java) {
                runBlocking { pinDao.insertPin(pin) }
            }
        }

    @Test
    fun insertDuplicatePinId_shouldThrowConstraintException() =
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

            assertThrows(SQLiteConstraintException::class.java) {
                runBlocking { pinDao.insertPin(pin) }
            }
        }

    @Test
    fun deleteCategory_withAssociatedPin_shouldThrowConstraintException() =
        runBlocking {
            // 1. Insert category and pin
            val catId = "protected_cat"
            categoryDao.insertCategory(CategoryEntity(catId, false))
            val pin =
                PinEntity(
                    id = "pin_linked",
                    latitude = 1.0,
                    longitude = 1.0,
                    categoryId = catId,
                    accuracyMeters = null,
                    altitudeMeters = null,
                    createdAtEpochMillis = 1000L,
                    updatedAtEpochMillis = 1000L,
                )
            pinDao.insertPin(pin)

            // 2. Attempt to delete category via raw SQL since DAO has no delete yet
            assertThrows(SQLiteConstraintException::class.java) {
                db.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = '$catId'")
            }

            // 3. Verify Pin still exists
            val observed = pinDao.observePin("pin_linked").first()
            assertNotNull("Pin should still exist after failed category deletion", observed)
        }
}
