package com.laurentvrevin.wheris.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.data.mapper.toEntity
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PinPhotoLifecycleTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var database: WherisDatabase
    private lateinit var storage: AndroidPhotoStorage
    private lateinit var repository: PinRepositoryImpl
    private var failCleanup = false
    private var failDbDelete = false

    @Before
    fun setUp() {
        cleanFiles()
        database =
            Room.inMemoryDatabaseBuilder(context, WherisDatabase::class.java)
                .addCallback(WherisDatabase.getCallback()).build()
        storage =
            AndroidPhotoStorage(context) { file ->
                if (failCleanup && file.parentFile?.name == "photo_pending_delete") false else file.delete()
            }
        val dao =
            object : PinDao by database.pinDao() {
                override suspend fun deletePin(pinId: String) {
                    if (failDbDelete) throw IOException("Injected DB failure")
                    database.pinDao().deletePin(pinId)
                }
            }
        repository = PinRepositoryImpl(dao, storage)
    }

    @After
    fun tearDown() {
        database.close()
        cleanFiles()
    }

    private fun cleanFiles() {
        listOf(File(context.cacheDir, "photo_drafts"), File(context.filesDir, "photos"), File(context.filesDir, "photo_pending_delete"))
            .forEach { it.deleteRecursively() }
        File(context.cacheDir, "fixture.png").delete()
    }

    private suspend fun pinWithPhoto(): Pin {
        val fixture = File(context.cacheDir, "fixture.png")
        Bitmap.createBitmap(40, 60, Bitmap.Config.ARGB_8888).apply {
            fixture.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
        val draft = storage.importPhoto(Uri.fromFile(fixture))
        val reference = storage.promote(draft)
        return CreatePinUseCase(repository, { PinId("photo-pin") })(
            UserLocation(GeoPoint(10.0, 20.0), timestampEpochMillis = 1000L),
            SystemCategoryIds.OTHER,
            photoReference = reference,
        )
    }

    @Test
    fun missingOrKnownCorruptPermanentPhotoNeverCreatesARoomRow() =
        runBlocking {
            val location = UserLocation(GeoPoint(10.0, 20.0), timestampEpochMillis = 1000L)
            val create = CreatePinUseCase(repository)
            try {
                create(location, SystemCategoryIds.OTHER, photoReference = PhotoReference("missing"))
                fail("Missing photo accepted")
            } catch (_: IOException) {
            }
            File(context.filesDir, "photos/corrupt").apply {
                parentFile!!.mkdirs()
                writeText("not an image")
            }
            try {
                create(location, SystemCategoryIds.OTHER, photoReference = PhotoReference("corrupt"))
                fail("Known corrupt photo accepted")
            } catch (_: IOException) {
            }
            assertTrue(database.pinDao().observePins().first().isEmpty())
        }

    private fun permanent(pin: Pin) = File(context.filesDir, "photos/${pin.photoReference!!.value}")

    private fun pending(pin: Pin) = File(context.filesDir, "photo_pending_delete/${pin.photoReference!!.value}")

    @Test
    fun deleteRemovesBothRoomRowAndRealPhoto() =
        runBlocking {
            val pin = pinWithPhoto()
            assertTrue(permanent(pin).exists())
            repository.deletePin(pin.id)
            assertNull(repository.observePin(pin.id).first())
            assertFalse(permanent(pin).exists())
            assertFalse(pending(pin).exists())
        }

    @Test
    fun databaseFailureRestoresPhotoAndPreservesPin() =
        runBlocking {
            val pin = pinWithPhoto()
            val bytes = permanent(pin).readBytes()
            failDbDelete = true
            try {
                repository.deletePin(pin.id)
                fail("Failure swallowed")
            } catch (_: IOException) {
            }
            assertEquals(pin, repository.observePin(pin.id).first())
            assertArrayEquals(bytes, permanent(pin).readBytes())
            assertFalse(pending(pin).exists())
        }

    @Test
    fun finalCleanupFailureLeavesDurablePendingStateAndAbsentPinRetryFinishesIt() =
        runBlocking {
            val pin = pinWithPhoto()
            failCleanup = true
            try {
                repository.deletePin(pin.id)
                fail("Cleanup failure swallowed")
            } catch (_: IOException) {
            }
            assertNull(database.pinDao().observePin(pin.id.value).first())
            assertTrue(pending(pin).exists())
            assertFalse(permanent(pin).exists())
            failCleanup = false
            repository.deletePin(pin.id)
            assertFalse(pending(pin).exists())
            repository.deletePin(pin.id)
        }

    @Test
    fun restartedRepositoryRestoresPendingPhotoWhenRowExists() =
        runBlocking {
            val pin = pinWithPhoto()
            storage.stageDeletion(pin.photoReference!!)
            val restarted = PinRepositoryImpl(database.pinDao(), AndroidPhotoStorage(context))
            assertEquals(pin, restarted.observePin(pin.id).first())
            assertTrue(permanent(pin).exists())
            assertFalse(pending(pin).exists())
        }

    @Test
    fun restartedRepositoryCleansPendingPhotoWhenRowWasDeleted() =
        runBlocking {
            val pin = pinWithPhoto()
            storage.stageDeletion(pin.photoReference!!)
            database.pinDao().deletePin(pin.id.value)
            val restarted = PinRepositoryImpl(database.pinDao(), AndroidPhotoStorage(context))
            assertTrue(restarted.observePins().first().isEmpty())
            assertFalse(pending(pin).exists())
        }

    @Test
    fun historicalPinWithoutPhotoStillDeletesIdempotently() =
        runBlocking {
            val pin =
                CreatePinUseCase(repository)(
                    UserLocation(GeoPoint(10.0, 20.0), timestampEpochMillis = 1000L),
                    SystemCategoryIds.OTHER,
                )
            repository.deletePin(pin.id)
            repository.deletePin(pin.id)
            assertTrue(repository.observePins().first().isEmpty())
        }

    @Test
    fun photoCannotBeSharedByTwoPins() =
        runBlocking {
            val pin = pinWithPhoto()
            try {
                repository.savePin(pin.copy(id = PinId("other")))
                fail("Shared photo accepted")
            } catch (_: IllegalStateException) {
            }
            assertEquals(listOf(pin), repository.observePins().first())
            assertTrue(permanent(pin).exists())
        }

    @Test
    fun unexpectedLegacySharedReferenceKeepsPhotoUntilLastPinIsDeleted() =
        runBlocking {
            val pin = pinWithPhoto()
            val other = pin.copy(id = PinId("legacy-other"))
            database.pinDao().insertPin(other.toEntity())
            repository.deletePin(pin.id)
            assertTrue(permanent(pin).exists())
            assertEquals(other, repository.observePin(other.id).first())
            repository.deletePin(other.id)
            assertFalse(permanent(pin).exists())
            assertTrue(repository.observePins().first().isEmpty())
        }
}
