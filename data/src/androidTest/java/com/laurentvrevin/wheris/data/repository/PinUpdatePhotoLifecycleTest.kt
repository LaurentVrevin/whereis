package com.laurentvrevin.wheris.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.dao.PinMutationStatus
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PinUpdatePhotoLifecycleTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var db: WherisDatabase
    private lateinit var storage: AndroidPhotoStorage
    private lateinit var photos: RecordingStorage
    private lateinit var repository: PinRepositoryImpl
    private var failCleanup = false
    private var failDb = false
    private var cancelDb = false
    private var failRestore = false
    private var afterStage: (() -> Unit)? = null
    private var mutationStarted: CompletableDeferred<Unit>? = null
    private var finishMutation: CompletableDeferred<Unit>? = null

    @Before fun setUp() {
        clean()
        db =
            Room.inMemoryDatabaseBuilder(context, WherisDatabase::class.java)
                .addCallback(WherisDatabase.getCallback()).build()
        storage =
            AndroidPhotoStorage(context) { file ->
                if (failCleanup && file.parentFile?.name == "photo_pending_delete") false else file.delete()
            }
        photos = RecordingStorage(storage)
        val dao =
            object : PinDao by db.pinDao() {
                override suspend fun mutatePin(
                    pinId: String,
                    categoryId: String,
                    name: String?,
                    note: String?,
                    isFavorite: Boolean,
                    photoReference: String?,
                    expectedPhotoReference: String?,
                    updatedAt: Long,
                ): PinMutationStatus {
                    mutationStarted?.complete(Unit)
                    finishMutation?.await()
                    if (cancelDb) throw CancellationException("Injected cancellation")
                    if (failDb) throw IOException("Injected DB failure")
                    return db.pinDao().mutatePin(
                        pinId,
                        categoryId,
                        name,
                        note,
                        isFavorite,
                        photoReference,
                        expectedPhotoReference,
                        updatedAt,
                    )
                }
            }
        repository = PinRepositoryImpl(dao, photos)
    }

    @After fun tearDown() {
        db.close()
        clean()
    }

    private fun clean() {
        listOf(File(context.cacheDir, "photo_drafts"), File(context.filesDir, "photos"), File(context.filesDir, "photo_pending_delete"))
            .forEach { it.deleteRecursively() }
        File(context.cacheDir, "update-fixture.png").delete()
    }

    private suspend fun replacement(): Pair<PhotoDraftReference, PhotoReference> {
        val fixture = File(context.cacheDir, "update-fixture.png")
        Bitmap.createBitmap(40, 60, Bitmap.Config.ARGB_8888).apply {
            fixture.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
        val draft = storage.importPhoto(Uri.fromFile(fixture))
        return draft to storage.promote(draft)
    }

    private suspend fun original(withPhoto: Boolean = true): Pin {
        val pin =
            CreatePinUseCase(repository, { PinId("edit") }, { 2000L })(
                UserLocation(GeoPoint(10.0, 20.0), 7f, 30.0, 1000L),
                SystemCategoryIds.CAR,
                "Original",
                "Original note",
                photoReference = if (withPhoto) replacement().second else null,
            )
        photos.calls.clear()
        return pin
    }

    private fun update(
        pin: Pin,
        change: PinPhotoChange,
    ): PinUpdate = PinUpdate(pin.id, SystemCategoryIds.PARKING, "Edited", "New note", true, change)

    private suspend fun mutate(
        pin: Pin,
        change: PinPhotoChange,
    ) = UpdatePinUseCase(repository) { 5000L }(update(pin, change))

    private suspend fun row(pin: Pin) = db.pinDao().getPin(pin.id.value)?.toDomain()

    private fun permanent(photo: PhotoReference) = File(context.filesDir, "photos/${photo.value}")

    private fun pending(photo: PhotoReference) = File(context.filesDir, "photo_pending_delete/${photo.value}")

    private fun expected(
        pin: Pin,
        photo: PhotoReference?,
    ) = pin.copy(
        categoryId = SystemCategoryIds.PARKING,
        name = "Edited",
        note = "New note",
        isFavorite = true,
        photoReference = photo,
        updatedAtEpochMillis = 5000L,
    )

    @Test fun keepDoesNotCallAnyPhotoOperationEvenBeforeFirstObservation() =
        runBlocking {
            val pin = original()
            repository = PinRepositoryImpl(db.pinDao(), photos)
            val bytes = permanent(pin.photoReference!!).readBytes()
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Keep))
            assertTrue(photos.calls.isEmpty())
            assertEquals(expected(pin, pin.photoReference), row(pin))
            assertArrayEquals(bytes, permanent(pin.photoReference!!).readBytes())
        }

    @Test fun removeCommitsNullAndDeletesOldFile() =
        runBlocking {
            val pin = original()
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Remove))
            assertEquals(expected(pin, null), row(pin))
            assertFalse(permanent(pin.photoReference!!).exists())
            assertFalse(pending(pin.photoReference!!).exists())
            assertEquals(listOf("stage", "finish"), photos.calls)
        }

    @Test fun removeDatabaseFailureRestoresBytesAndEveryColumn() =
        runBlocking {
            val pin = original()
            val bytes = permanent(pin.photoReference!!).readBytes()
            // Real SQLite rollback, not only a fake DAO failure.
            db.openHelper.writableDatabase.execSQL(
                "CREATE TRIGGER fail_update AFTER UPDATE ON pins BEGIN SELECT RAISE(ABORT, 'injected failure'); END",
            )
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Remove))
            assertEquals(pin, row(pin))
            assertArrayEquals(bytes, permanent(pin.photoReference!!).readBytes())
            assertFalse(pending(pin.photoReference!!).exists())
            assertEquals(listOf("stage", "restore"), photos.calls)
        }

    @Test fun removeCleanupFailureReportsCommittedAndNextObservationReconciles() =
        runBlocking {
            val pin = original()
            failCleanup = true
            assertEquals(PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING, mutate(pin, PinPhotoChange.Remove))
            assertEquals(expected(pin, null), row(pin))
            assertTrue(pending(pin.photoReference!!).exists())
            failCleanup = false
            repository.observePin(pin.id).first()
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test fun replaceCommitsNewPhotoTransfersOwnershipAndDeletesOld() =
        runBlocking {
            val pin = original()
            val (draft, photo) = replacement()
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Replace(photo)))
            assertEquals(expected(pin, photo), row(pin))
            assertEquals(listOf("verify", "stage", "attached", "finish"), photos.calls)
            storage.discard(draft)
            assertTrue(permanent(photo).exists())
            assertFalse(permanent(pin.photoReference!!).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test fun replaceDatabaseFailureRestoresOldAndKeepsNewLeasedForRetry() =
        runBlocking {
            val pin = original()
            val (draft, photo) = replacement()
            val bytes = permanent(pin.photoReference!!).readBytes()
            failDb = true
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Replace(photo)))
            assertEquals(pin, row(pin))
            assertEquals(listOf("verify", "stage", "restore"), photos.calls)
            assertArrayEquals(bytes, permanent(pin.photoReference!!).readBytes())
            storage.reconcile(setOf(pin.photoReference!!))
            assertEquals(photo, storage.promote(draft))
            failDb = false
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Replace(photo)))
            assertEquals(expected(pin, photo), row(pin))
            storage.discard(draft)
            assertTrue(permanent(photo).exists())
        }

    @Test fun replaceCleanupFailureRetainsNewCommitAndRecoversOldOnFreshProcess() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            failCleanup = true
            assertEquals(PinUpdateResult.SUCCESS_WITH_CLEANUP_PENDING, mutate(pin, PinPhotoChange.Replace(photo)))
            assertEquals(expected(pin, photo), row(pin))
            assertTrue(pending(pin.photoReference!!).exists())
            AndroidPhotoStorage(context).reconcile(setOf(photo))
            assertTrue(permanent(photo).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test fun sameReferenceReplacementIsLogicalKeepWithoutFileAccess() =
        runBlocking {
            val pin = original()
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Replace(pin.photoReference!!)))
            assertTrue(photos.calls.isEmpty())
            assertEquals(expected(pin, pin.photoReference), row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun attachedReplacementIsRejectedWithoutAnyFileOperationOrMutation() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            val other =
                CreatePinUseCase(repository, { PinId("other") })(
                    UserLocation(GeoPoint(11.0, 21.0), timestampEpochMillis = 1000L),
                    SystemCategoryIds.OTHER,
                    photoReference = photo,
                )
            photos.calls.clear()
            assertEquals(PinUpdateResult.PHOTO_ALREADY_ATTACHED, mutate(pin, PinPhotoChange.Replace(photo)))
            assertTrue(photos.calls.isEmpty())
            assertEquals(pin, row(pin))
            assertEquals(other, row(other))
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun missingPinDoesNotTouchFilesOrAttachReplacement() =
        runBlocking {
            val pin = original()
            val (draft, photo) = replacement()
            assertEquals(
                PinUpdateResult.PIN_NOT_FOUND,
                UpdatePinUseCase(repository)(update(pin, PinPhotoChange.Replace(photo)).copy(pinId = PinId("missing"))),
            )
            assertTrue(photos.calls.isEmpty())
            assertEquals(pin, row(pin))
            assertEquals(photo, storage.promote(draft))
        }

    @Test fun missingCategoryDoesNotStageOldOrAttachReplacement() =
        runBlocking {
            val pin = original()
            val (draft, photo) = replacement()
            assertEquals(
                PinUpdateResult.CATEGORY_NOT_FOUND,
                UpdatePinUseCase(repository)(update(pin, PinPhotoChange.Replace(photo)).copy(categoryId = CategoryId("missing"))),
            )
            assertTrue(photos.calls.isEmpty())
            assertEquals(pin, row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
            assertEquals(photo, storage.promote(draft))
        }

    @Test fun missingOrCorruptReplacementCannotChangePinOrRetireOld() =
        runBlocking {
            val pin = original()
            for (photo in listOf(PhotoReference("missing"), PhotoReference("corrupt"))) {
                if (photo.value == "corrupt") permanent(photo).writeText("not an image")
                assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Replace(photo)))
                assertEquals(pin, row(pin))
                assertTrue(permanent(pin.photoReference!!).exists())
            }
            assertEquals(listOf("verify", "verify"), photos.calls)
        }

    @Test fun removeWithoutOldPhotoUsesNoFileOperations() =
        runBlocking {
            val pin = original(withPhoto = false)
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Remove))
            assertEquals(expected(pin, null), row(pin))
            assertTrue(photos.calls.isEmpty())
        }

    @Test fun replaceWithoutOldPhotoOnlyValidatesAndAttaches() =
        runBlocking {
            val pin = original(withPhoto = false)
            val (_, photo) = replacement()
            assertEquals(PinUpdateResult.SUCCESS, mutate(pin, PinPhotoChange.Replace(photo)))
            assertEquals(expected(pin, photo), row(pin))
            assertEquals(listOf("verify", "attached"), photos.calls)
        }

    @Test fun processInterruptionAfterStageBeforeCommitRestoresOldAndCleansUnreferencedNew() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            storage.stageDeletion(pin.photoReference!!)
            AndroidPhotoStorage(context).reconcile(setOf(pin.photoReference!!))
            assertEquals(pin, row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
            assertFalse(pending(pin.photoReference!!).exists())
            assertFalse(permanent(photo).exists())
        }

    @Test fun processInterruptionAfterCommitBeforeHandoffAndCleanupKeepsNewAndCleansOld() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            storage.stageDeletion(pin.photoReference!!)
            assertEquals(
                PinMutationStatus.SUCCESS,
                db.pinDao().mutatePin(
                    pin.id.value,
                    "parking",
                    "Edited",
                    "New note",
                    true,
                    photo.value,
                    pin.photoReference!!.value,
                    5000L,
                ),
            )
            AndroidPhotoStorage(context).reconcile(setOf(photo))
            assertEquals(expected(pin, photo), row(pin))
            assertTrue(permanent(photo).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test fun failedReplacementLeaseSurvivesLiveRecoveryButNotProcessDeath() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            failDb = true
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Replace(photo)))
            storage.reconcile(setOf(pin.photoReference!!))
            assertTrue(permanent(photo).exists())
            AndroidPhotoStorage(context).reconcile(setOf(pin.photoReference!!))
            assertFalse(permanent(photo).exists())
            assertTrue(permanent(pin.photoReference!!).exists())
            assertEquals(pin, row(pin))
        }

    @Test fun cancellationBeforeCommitPropagatesAfterRestoringOld() =
        runBlocking {
            val pin = original()
            val (_, photo) = replacement()
            cancelDb = true
            try {
                mutate(pin, PinPhotoChange.Replace(photo))
                fail("Cancellation swallowed")
            } catch (_: CancellationException) {
            }
            assertEquals(pin, row(pin))
            assertEquals(listOf("verify", "stage", "restore"), photos.calls)
            assertTrue(permanent(photo).exists())
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun failedRestoreLeavesDurablePendingPhotoThatReconciliationRestores() =
        runBlocking {
            val pin = original()
            failDb = true
            failRestore = true
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Remove))
            assertEquals(pin, row(pin))
            assertTrue(pending(pin.photoReference!!).exists())
            AndroidPhotoStorage(context).reconcile(setOf(pin.photoReference!!))
            assertTrue(permanent(pin.photoReference!!).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test fun categoryDisappearingAfterPreflightReturnsMissingAndRestoresOld() =
        runBlocking {
            val pin = original()
            afterStage = { db.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = 'parking'") }
            assertEquals(PinUpdateResult.CATEGORY_NOT_FOUND, mutate(pin, PinPhotoChange.Remove))
            assertEquals(pin, row(pin))
            assertEquals(listOf("stage", "restore"), photos.calls)
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test
    fun callerCancellationAfterStagingStillFinishesCommitAndOwnershipHandoff() =
        runBlocking {
            val pin = original()
            val (draft, photo) = replacement()
            mutationStarted = CompletableDeferred()
            finishMutation = CompletableDeferred()
            var cancellationPropagated = false
            val job =
                launch {
                    try {
                        mutate(pin, PinPhotoChange.Replace(photo))
                    } catch (exception: CancellationException) {
                        cancellationPropagated = true
                        throw exception
                    }
                }
            mutationStarted!!.await()
            job.cancel()
            finishMutation!!.complete(Unit)
            job.join()
            assertTrue(cancellationPropagated)
            assertEquals(expected(pin, photo), row(pin))
            assertEquals(listOf("verify", "stage", "attached", "finish"), photos.calls)
            storage.discard(draft)
            assertTrue(permanent(photo).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    @Test
    fun stagingFailureAfterMovingOldBytesRestoresBeforeReturningFailure() =
        runBlocking {
            val pin = original()
            afterStage = { throw IOException("Injected staging failure") }
            assertEquals(PinUpdateResult.TECHNICAL_FAILURE, mutate(pin, PinPhotoChange.Remove))
            assertEquals(pin, row(pin))
            assertEquals(listOf("stage", "restore"), photos.calls)
            assertTrue(permanent(pin.photoReference!!).exists())
            assertFalse(pending(pin.photoReference!!).exists())
        }

    private inner class RecordingStorage(private val delegate: PhotoStorage) : PhotoStorage by delegate {
        val calls = mutableListOf<String>()

        override suspend fun verifyPermanent(photo: PhotoReference) {
            calls += "verify"
            delegate.verifyPermanent(photo)
        }

        override suspend fun attached(photo: PhotoReference) {
            calls += "attached"
            delegate.attached(photo)
        }

        override suspend fun stageDeletion(photo: PhotoReference) {
            calls += "stage"
            delegate.stageDeletion(photo)
            afterStage?.invoke()
        }

        override suspend fun restoreDeletion(photo: PhotoReference) {
            calls += "restore"
            if (failRestore) throw IOException("Injected restore failure")
            delegate.restoreDeletion(photo)
        }

        override suspend fun finishDeletion(photo: PhotoReference) {
            calls += "finish"
            delegate.finishDeletion(photo)
        }

        override suspend fun reconcile(attached: Set<PhotoReference>) {
            calls += "reconcile"
            delegate.reconcile(attached)
        }
    }
}
