package com.laurentvrevin.wheris.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), WherisDatabase::class.java)

    @Test
    fun migrationPreservesPlacesAndAllowsEditingOnlyText() =
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
            helper.runMigrationsAndValidate(databaseName, 2, true, MIGRATION_1_2).close()
            val database =
                Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                    .addMigrations(MIGRATION_1_2)
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
                val category = database.categoryDao().getCategoryById("custom-category")
                assertNotNull(category)
                assertEquals(false, category?.isSystem)

                assertEquals(1, database.pinDao().updateDetails("old-pin", "Camping", "Près du chemin", 3000L))
                assertEquals(
                    original.copy(name = "Camping", note = "Près du chemin", updatedAtEpochMillis = 3000L),
                    database.pinDao().observePin("old-pin").first(),
                )
                assertEquals(0, database.pinDao().updateDetails("missing", "Absent", null, 4000L))
                assertEquals(1, database.pinDao().observePins().first().size)
            } finally {
                database.close()
                context.deleteDatabase(databaseName)
            }
        }
}
