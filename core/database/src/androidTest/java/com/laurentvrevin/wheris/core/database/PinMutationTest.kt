package com.laurentvrevin.wheris.core.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.dao.PinMutationStatus
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PinMutationTest {
    private lateinit var db: WherisDatabase
    private val original =
        PinEntity(
            "edit", 10.0, 20.0, "car", 7f, 30.0, 1000L, 2000L,
            "Original", "Note", false, "photo-a",
        )

    @Before fun setUp() =
        runBlocking {
            db =
                Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), WherisDatabase::class.java)
                    .addCallback(WherisDatabase.getCallback()).build()
            db.pinDao().insertPin(original)
        }

    @After fun tearDown() {
        db.close()
    }

    private suspend fun mutate(
        id: String = original.id,
        category: String = "parking",
        photo: String? = "photo-b",
        expected: String? = original.photoReference,
    ): PinMutationStatus = db.pinDao().mutatePin(id, category, "Edited", "New note", true, photo, expected, 5000L)

    @Test fun allEditableFieldsChangeTogetherAndGeographicFieldsStayExact() =
        runBlocking {
            assertEquals(PinMutationStatus.SUCCESS, mutate())
            assertEquals(
                original.copy(
                    categoryId = "parking",
                    name = "Edited",
                    note = "New note",
                    isFavorite = true,
                    photoReference = "photo-b",
                    updatedAtEpochMillis = 5000L,
                ),
                db.pinDao().getPin(original.id),
            )
            db.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
        }

    @Test fun missingCategoryLeavesEveryColumnUnchanged() =
        runBlocking {
            assertEquals(PinMutationStatus.CATEGORY_NOT_FOUND, mutate(category = "missing"))
            assertEquals(original, db.pinDao().getPin(original.id))
        }

    @Test fun missingPinCannotInsertOrChangeAnotherPin() =
        runBlocking {
            assertEquals(PinMutationStatus.PIN_NOT_FOUND, mutate(id = "missing"))
            assertEquals(original, db.pinDao().getPin(original.id))
            assertEquals(null, db.pinDao().getPin("missing"))
        }

    @Test fun failedUpdateRollsBackEveryColumn() {
        db.openHelper.writableDatabase.execSQL(
            "CREATE TRIGGER fail_pin_update AFTER UPDATE ON pins BEGIN SELECT RAISE(ABORT, 'injected failure'); END",
        )
        assertThrows(SQLiteConstraintException::class.java) { runBlocking { mutate() } }
        runBlocking { assertEquals(original, db.pinDao().getPin(original.id)) }
    }

    @Test fun categoryForeignKeyRemainsRestrict() {
        assertThrows(SQLiteConstraintException::class.java) {
            db.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = 'car'")
        }
        runBlocking { assertEquals(original, db.pinDao().getPin(original.id)) }
    }

    @Test fun changedPhotoSincePreflightCannotBeOverwritten() =
        runBlocking {
            assertEquals(PinMutationStatus.CONFLICT, mutate(expected = "stale-photo"))
            assertEquals(original, db.pinDao().getPin(original.id))
        }

    @Test fun replacementSharingIsRecheckedInTransaction() =
        runBlocking {
            db.pinDao().insertPin(original.copy(id = "other", photoReference = "photo-b"))
            assertEquals(PinMutationStatus.PHOTO_ALREADY_ATTACHED, mutate())
            assertEquals(original, db.pinDao().getPin(original.id))
        }
}
