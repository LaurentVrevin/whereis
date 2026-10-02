package com.laurentvrevin.wheris.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laurentvrevin.wheris.core.database.dao.PinMutationStatus
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    @Test
    fun migrationTwoToThreePreservesCategoriesPlacesTextAndForeignKeys() =
        runBlocking {
            val databaseName = "migration-2-3-test"
            val legacyId = "\u2003 legacy-custom \u2003"
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            helper.createDatabase(databaseName, 2).apply {
                com.laurentvrevin.wheris.core.model.SystemCategoryIds.ALL.forEach { id ->
                    execSQL("INSERT INTO categories (id, isSystem) VALUES (?, 1)", arrayOf(id.value))
                }
                execSQL("INSERT INTO categories (id, isSystem) VALUES (?, 0)", arrayOf(legacyId))
                listOf("car", "other", legacyId).forEach { categoryId ->
                    execSQL(
                        "INSERT INTO pins (id, latitude, longitude, categoryId, accuracyMeters, altitudeMeters, " +
                            "createdAtEpochMillis, updatedAtEpochMillis, name, note) VALUES (?, 10, 20, ?, 7, 30, 1000, 2000, ?, ?)",
                        arrayOf("pin-$categoryId", categoryId, "Nom $categoryId", "Note $categoryId"),
                    )
                }
                close()
            }
            helper.runMigrationsAndValidate(databaseName, 3, true, MIGRATION_2_3).close()
            val database =
                Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(WherisDatabase.getCallback())
                    .build()
            try {
                val categories = database.categoryDao().observeCategories().first()
                assertEquals(
                    com.laurentvrevin.wheris.core.model.SystemCategoryIds.ALL.map { it.value } + legacyId,
                    categories.map { it.id },
                )
                categories.filter { it.isSystem }.forEach {
                    assertNull(it.name)
                    assertNull(it.iconKey)
                    assertNull(it.colorKey)
                    assertNull(it.createdAtEpochMillis)
                }
                val custom = requireNotNull(database.categoryDao().getCategoryById(legacyId))
                assertEquals("legacy-custom", custom.name)
                assertEquals("place", custom.iconKey)
                assertEquals("category/orange", custom.colorKey)
                assertEquals(0L, custom.createdAtEpochMillis)
                val pins = database.pinDao().observePins().first()
                assertEquals(3, pins.size)
                pins.forEach { pin ->
                    assertEquals("pin-${pin.categoryId}", pin.id)
                    assertEquals("Nom ${pin.categoryId}", pin.name)
                    assertEquals("Note ${pin.categoryId}", pin.note)
                    assertEquals(10.0, pin.latitude, 0.0)
                    assertEquals(20.0, pin.longitude, 0.0)
                    assertEquals(7f, pin.accuracyMeters)
                    assertEquals(30.0, pin.altitudeMeters)
                    assertEquals(1000L, pin.createdAtEpochMillis)
                    assertEquals(2000L, pin.updatedAtEpochMillis)
                    assertEquals(false, pin.isFavorite)
                    assertNull(pin.photoReference)
                }
                val sqlDb = database.openHelper.writableDatabase
                sqlDb.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
                org.junit.Assert.assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
                    sqlDb.execSQL("DELETE FROM categories WHERE id = ?", arrayOf(legacyId))
                }
                org.junit.Assert.assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
                    runBlocking { database.pinDao().insertPin(pins.first().copy(id = "invalid", categoryId = "missing")) }
                }
                assertEquals(3, database.pinDao().observePins().first().size)
            } finally {
                database.close()
                context.deleteDatabase(databaseName)
            }
        }

    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), WherisDatabase::class.java)

    @Test
    fun migrationOneToFourPreservesPlacesAndAllowsEditingOnlyText() =
        runBlocking {
            val databaseName = "migration-1-2-test"
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            helper.createDatabase(databaseName, 1).apply {
                execSQL("INSERT INTO categories (id, isSystem) VALUES ('custom-category', 0)")
                execSQL(
                    "INSERT INTO pins (id, latitude, longitude, categoryId, accuracyMeters, altitudeMeters, " +
                        "createdAtEpochMillis, updatedAtEpochMillis) " +
                        "VALUES ('old-pin', 10.0, 20.0, 'custom-category', 7.0, 30.0, 1000, 2000)",
                )
                close()
            }
            helper.runMigrationsAndValidate(databaseName, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).close()
            val database =
                Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(WherisDatabase.getCallback())
                    .build()
            try {
                // Reopening through Room also checks that the migrated data can be read and edited.
                val original = requireNotNull(database.pinDao().observePin("old-pin").first())
                assertEquals(10.0, original.latitude, 0.0)
                assertEquals(20.0, original.longitude, 0.0)
                assertEquals("custom-category", original.categoryId)
                assertEquals(7f, original.accuracyMeters)
                assertEquals(30.0, original.altitudeMeters)
                assertEquals(1000L, original.createdAtEpochMillis)
                assertEquals(2000L, original.updatedAtEpochMillis)
                assertNull(original.name)
                assertNull(original.note)
                assertEquals(false, original.isFavorite)
                assertNull(original.photoReference)
                val category = database.categoryDao().getCategoryById("custom-category")
                assertNotNull(category)
                assertEquals(false, category?.isSystem)
                assertEquals(
                    SystemCategoryIds.ALL.map { it.value },
                    database.categoryDao().observeSystemCategories().first().map { it.id },
                )

                assertEquals(
                    PinMutationStatus.SUCCESS,
                    database.pinDao().mutatePin("old-pin", original.categoryId, "Camping", "Près du chemin", false, null, null, 3000L),
                )
                assertEquals(
                    original.copy(name = "Camping", note = "Près du chemin", updatedAtEpochMillis = 3000L),
                    database.pinDao().observePin("old-pin").first(),
                )
                assertEquals(
                    PinMutationStatus.PIN_NOT_FOUND,
                    database.pinDao().mutatePin("missing", original.categoryId, "Absent", null, false, null, null, 4000L),
                )
                assertEquals(1, database.pinDao().observePins().first().size)
            } finally {
                database.close()
                context.deleteDatabase(databaseName)
            }
        }

    @Test
    fun migrationThreeToFourPreservesEveryFieldDefaultsAndConstraintsAfterReopen() =
        runBlocking {
            val databaseName = "migration-3-4-test"
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val expected =
                listOf(
                    PinEntity("a", 10.25, -20.5, "other", null, null, 1000L, 1100L),
                    PinEntity("b", -35.75, 40.5, "car", null, null, 2000L, 2200L, "Café 東京", "  Note\n第二行  "),
                    PinEntity("c", 50.25, 60.5, "custom", 7.5f, 35.25, 3000L, 3300L, "Custom", null),
                )
            helper.createDatabase(databaseName, 3).apply {
                SystemCategoryIds.ALL.forEach { id ->
                    execSQL("INSERT INTO categories (id, isSystem) VALUES (?, 1)", arrayOf(id.value))
                }
                execSQL(
                    "INSERT INTO categories (id, isSystem, name, iconKey, colorKey, createdAtEpochMillis) " +
                        "VALUES ('custom', 0, 'Camping', 'park', 'category/green', 500)",
                )
                expected.forEach { pin ->
                    execSQL(
                        "INSERT INTO pins (id, latitude, longitude, categoryId, accuracyMeters, altitudeMeters, " +
                            "createdAtEpochMillis, updatedAtEpochMillis, name, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        arrayOf(
                            pin.id, pin.latitude, pin.longitude, pin.categoryId, pin.accuracyMeters, pin.altitudeMeters,
                            pin.createdAtEpochMillis, pin.updatedAtEpochMillis, pin.name, pin.note,
                        ),
                    )
                }
                close()
            }
            helper.runMigrationsAndValidate(databaseName, 4, true, MIGRATION_3_4).close()
            try {
                repeat(2) {
                    val database =
                        Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .addCallback(WherisDatabase.getCallback()).build()
                    try {
                        assertEquals(expected, database.pinDao().observePins().first().sortedBy { it.id })
                        val custom = requireNotNull(database.categoryDao().getCategoryById("custom"))
                        assertEquals("Camping", custom.name)
                        assertEquals("park", custom.iconKey)
                        assertEquals("category/green", custom.colorKey)
                        assertEquals(500L, custom.createdAtEpochMillis)
                        assertSchemaFourConstraints(database)
                        org.junit.Assert.assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
                            database.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = 'custom'")
                        }
                        assertEquals(expected, database.pinDao().observePins().first().sortedBy { it.id })
                    } finally {
                        database.close()
                    }
                }
            } finally {
                context.deleteDatabase(databaseName)
            }
        }

    private fun assertSchemaFourConstraints(database: WherisDatabase) {
        val sqlDb = database.openHelper.writableDatabase
        assertEquals(4, sqlDb.version)
        sqlDb.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
        sqlDb.query("PRAGMA foreign_key_list(pins)").use {
            assertEquals(1, it.count)
            it.moveToFirst()
            assertEquals("categories", it.getString(it.getColumnIndexOrThrow("table")))
            assertEquals("categoryId", it.getString(it.getColumnIndexOrThrow("from")))
            assertEquals("id", it.getString(it.getColumnIndexOrThrow("to")))
            assertEquals("RESTRICT", it.getString(it.getColumnIndexOrThrow("on_delete")))
        }
        sqlDb.query("PRAGMA index_info(index_pins_categoryId)").use {
            assertEquals(1, it.count)
            it.moveToFirst()
            assertEquals("categoryId", it.getString(it.getColumnIndexOrThrow("name")))
        }
        sqlDb.query("PRAGMA table_info(pins)").use {
            val columns = mutableSetOf<String>()
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                columns += name
                if (name == "isFavorite") {
                    assertEquals("INTEGER", it.getString(it.getColumnIndexOrThrow("type")))
                    assertEquals(1, it.getInt(it.getColumnIndexOrThrow("notnull")))
                    assertEquals("0", it.getString(it.getColumnIndexOrThrow("dflt_value")))
                }
                if (name == "photoReference") {
                    assertEquals("TEXT", it.getString(it.getColumnIndexOrThrow("type")))
                    assertEquals(0, it.getInt(it.getColumnIndexOrThrow("notnull")))
                }
            }
            org.junit.Assert.assertTrue(columns.containsAll(listOf("isFavorite", "photoReference")))
        }
    }
}
