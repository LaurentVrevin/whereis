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
    fun seed_shouldProvideAllSystemCategoriesAutomatically() =
        runBlocking {
            db.openHelper.writableDatabase

            val categories = categoryDao.observeSystemCategories().first()

            assertEquals(SystemCategoryIds.ALL.size, categories.size)
            assertEquals(
                SystemCategoryIds.ALL.toSet(),
                categories.map { category -> category.id }.toSet().map { id ->
                    com.laurentvrevin.wheris.core.model.CategoryId(id)
                }.toSet(),
            )
            assertTrue(categories.all { it.isSystem })
        }

    @Test
    fun seed_isIdempotent() =
        runBlocking {
            val sqliteDb = db.openHelper.writableDatabase
            val callback = WherisDatabase.getCallback()

            callback.onOpen(sqliteDb)
            callback.onOpen(sqliteDb)

            val categories = categoryDao.observeSystemCategories().first()
            assertEquals(SystemCategoryIds.ALL.size, categories.size)
            assertEquals(SystemCategoryIds.ALL.size, categories.map { it.id }.toSet().size)
        }

    @Test
    fun reopeningExistingDatabase_restoresMissingSystemCategory() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val databaseName = "system-category-reopen-test.db"
            context.deleteDatabase(databaseName)

            try {
                val firstOpen =
                    Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                        .addCallback(WherisDatabase.getCallback())
                        .build()
                val missingId = SystemCategoryIds.CAR.value
                firstOpen.openHelper.writableDatabase.execSQL(
                    "DELETE FROM categories WHERE id = ?",
                    arrayOf(missingId),
                )
                assertEquals(null, firstOpen.categoryDao().getCategoryById(missingId))
                firstOpen.close()

                val reopened =
                    Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                        .addCallback(WherisDatabase.getCallback())
                        .build()
                val restored = reopened.categoryDao().getCategoryById(missingId)

                assertNotNull(restored)
                assertTrue(restored?.isSystem == true)
                reopened.close()
            } finally {
                context.deleteDatabase(databaseName)
            }
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

            assertThrows(SQLiteConstraintException::class.java) {
                db.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = '$catId'")
            }

            val observed = pinDao.observePin("pin_linked").first()
            assertNotNull("Pin should still exist after failed category deletion", observed)
        }
}
