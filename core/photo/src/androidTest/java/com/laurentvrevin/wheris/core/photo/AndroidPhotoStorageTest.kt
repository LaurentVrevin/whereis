package com.laurentvrevin.wheris.core.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
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

@RunWith(AndroidJUnit4::class)
class AndroidPhotoStorageTest {
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var storage: AndroidPhotoStorage

    @Before
    fun setUp() {
        clean()
        storage = AndroidPhotoStorage(context)
    }

    @After
    fun clean() {
        listOf(File(context.cacheDir, "photo_drafts"), File(context.filesDir, "photos"), File(context.filesDir, "photo_pending_delete"))
            .forEach { it.deleteRecursively() }
    }

    private fun image(
        file: File,
        width: Int = 60,
        height: Int = 100,
    ) {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.GREEN)
            file.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
    }

    @Test
    fun failedDraftRemovalKeepsRecoverableBytesAndAbandonReleasesThemForRecovery() =
        runBlocking {
            var failCleanup = true
            val failing =
                AndroidPhotoStorage(context) { file ->
                    if (failCleanup && file.parentFile?.name == "photo_pending_delete") false else file.delete()
                }
            val draft = failing.prepareCamera()
            image(File(context.cacheDir, "photo_drafts/camera/${draft.value}"))
            failing.validateCamera(draft)
            val reference = failing.promote(draft)
            try {
                failing.discard(draft)
                fail("Cleanup failure swallowed")
            } catch (_: java.io.IOException) {
            }
            assertTrue(File(context.filesDir, "photo_pending_delete/${draft.value}").exists())
            failing.preview(draft).recycle()
            failing.reconcile(emptySet())
            assertTrue(File(context.filesDir, "photo_pending_delete/${draft.value}").exists())
            assertEquals(reference, failing.promote(draft))
            assertTrue(File(context.filesDir, "photos/${draft.value}").exists())
            failCleanup = false
            failing.discard(draft)
            assertFalse(File(context.filesDir, "photos/${draft.value}").exists())
            assertFalse(File(context.filesDir, "photo_pending_delete/${draft.value}").exists())

            val abandoned = failing.prepareCamera()
            image(File(context.cacheDir, "photo_drafts/camera/${abandoned.value}"))
            failing.validateCamera(abandoned)
            failing.promote(abandoned)
            failCleanup = true
            try {
                failing.abandon(abandoned)
                fail("Cleanup failure swallowed")
            } catch (_: java.io.IOException) {
            }
            failCleanup = false
            failing.reconcile(emptySet())
            assertFalse(File(context.filesDir, "photo_pending_delete/${abandoned.value}").exists())
        }

    private suspend fun imported(): PhotoDraftReference {
        val source = storage.prepareCamera()
        val uri = storage.cameraUri(source)
        context.contentResolver.openOutputStream(uri)!!.use { output ->
            Bitmap.createBitmap(60, 100, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.GREEN)
                compress(Bitmap.CompressFormat.PNG, 100, output)
                recycle()
            }
        }
        return storage.importPhoto(uri).also { storage.discard(source) }
    }

    @Test
    fun invalidImportCleanupFailureReleasesUnreturnedDraftForReconciliation() =
        runBlocking {
            var failCleanup = true
            val failing = AndroidPhotoStorage(context) { file -> if (failCleanup) false else file.delete() }
            val source = failing.prepareCamera()
            File(context.cacheDir, "photo_drafts/camera/${source.value}").writeText("not an image")
            try {
                failing.importPhoto(failing.cameraUri(source))
                fail("Invalid import accepted")
            } catch (_: java.io.IOException) {
            }
            try {
                failing.abandon(source)
                fail("Cleanup failure swallowed")
            } catch (_: java.io.IOException) {
            }
            failCleanup = false
            failing.reconcile(emptySet())
            assertEquals(0, File(context.cacheDir, "photo_drafts").walkTopDown().count { it.isFile })
        }

    @Test
    fun pickerUriIsCopiedAndExternalSourceIsNoLongerNeeded() =
        runBlocking {
            val draft = imported()
            assertTrue(File(context.cacheDir, "photo_drafts/${draft.value}").isFile)
            val preview = storage.preview(draft)
            assertEquals(60, preview.width)
            assertEquals(100, preview.height)
            assertEquals(Color.GREEN, preview.getPixel(0, 0))
            preview.recycle()
        }

    @Test
    fun cameraProviderIsPrivateAndOnlyExposesCameraDrafts() =
        runBlocking {
            val draft = storage.prepareCamera()
            val uri = storage.cameraUri(draft)
            assertEquals("content", uri.scheme)
            assertEquals("${context.packageName}.photo-drafts", uri.authority)
            val provider = context.packageManager.resolveContentProvider(uri.authority!!, 0)!!
            assertFalse(provider.exported)
            assertTrue(provider.grantUriPermissions)
            val permanent =
                File(context.filesDir, "photos/forbidden").apply {
                    parentFile!!.mkdirs()
                    writeText("private")
                }
            try {
                FileProvider.getUriForFile(context, uri.authority!!, permanent)
                fail("Permanent photos must not be exposed")
            } catch (_: IllegalArgumentException) {
            }
            val picker = File(context.cacheDir, "photo_drafts/forbidden").apply { writeText("private") }
            try {
                FileProvider.getUriForFile(context, uri.authority!!, picker)
                fail("Picker drafts must not be exposed")
            } catch (_: IllegalArgumentException) {
            }
            storage.discard(draft)
            assertFalse(File(context.cacheDir, "photo_drafts/camera/${draft.value}").exists())
        }

    @Test
    fun successfulCameraAndPromotionPreserveContentsAndRemoveTemporaryFile() =
        runBlocking {
            val draft = storage.prepareCamera()
            val source = File(context.cacheDir, "photo_drafts/camera/${draft.value}")
            image(source)
            val bytes = source.readBytes()
            assertEquals(draft, storage.validateCamera(draft))
            val photo = storage.promote(draft)
            assertEquals(draft.value, photo.value)
            assertArrayEquals(bytes, File(context.filesDir, "photos/${photo.value}").readBytes())
            assertFalse(source.exists())
            assertEquals(photo, storage.promote(draft))
            assertEquals(1, File(context.filesDir, "photos").listFiles()!!.size)
            storage.preview(draft).recycle()
        }

    @Test
    fun invalidCameraTargetIsCleanedAndCannotBePromoted() =
        runBlocking {
            val draft = storage.prepareCamera()
            File(context.cacheDir, "photo_drafts/camera/${draft.value}").writeText("corrupted")
            try {
                storage.validateCamera(draft)
                fail("Corrupt image accepted")
            } catch (_: java.io.IOException) {
            }
            assertFalse(File(context.cacheDir, "photo_drafts/camera/${draft.value}").exists())
            try {
                storage.promote(draft)
                fail("Invalid draft promoted")
            } catch (_: IllegalStateException) {
            }
        }

    @Test
    fun removingDraftAndPromotedPendingDraftCleansBothWithoutRemovingAttachedPhotos() =
        runBlocking {
            val draft = imported()
            storage.discard(draft)
            assertFalse(File(context.cacheDir, "photo_drafts/${draft.value}").exists())
            val promoted = imported()
            storage.promote(promoted)
            storage.discard(promoted)
            assertFalse(File(context.filesDir, "photos/${promoted.value}").exists())
            val attached = imported()
            val reference = storage.promote(attached)
            storage.attached(reference)
            storage.discard(attached)
            assertTrue(File(context.filesDir, "photos/${reference.value}").exists())
        }

    @Test
    fun reconciliationKeepsReferencesAndActiveLeasesAndRemovesInterruptedOrphans() =
        runBlocking {
            val draft = imported()
            val reference = storage.promote(draft)
            storage.reconcile(emptySet())
            assertTrue(File(context.filesDir, "photos/${reference.value}").exists())
            val restarted = AndroidPhotoStorage(context)
            restarted.reconcile(setOf(reference))
            assertTrue(File(context.filesDir, "photos/${reference.value}").exists())
            restarted.reconcile(emptySet())
            assertFalse(File(context.filesDir, "photos/${reference.value}").exists())
        }

    @Test
    fun interruptedDeletionRestoresReferencedPhotoAndCleansUnreferencedPhoto() =
        runBlocking {
            val attached = storage.promote(imported())
            storage.attached(attached)
            storage.stageDeletion(attached)
            AndroidPhotoStorage(context).reconcile(setOf(attached))
            assertTrue(File(context.filesDir, "photos/${attached.value}").exists())
            storage.stageDeletion(attached)
            AndroidPhotoStorage(context).reconcile(emptySet())
            assertFalse(File(context.filesDir, "photos/${attached.value}").exists())
            assertFalse(File(context.filesDir, "photo_pending_delete/${attached.value}").exists())
        }

    @Test
    fun largeLandscapeIsSampledAndPortraitPreviewIsValid(): Unit =
        runBlocking {
            val source = storage.prepareCamera()
            image(File(context.cacheDir, "photo_drafts/camera/${source.value}"), 4000, 2000)
            storage.validateCamera(source)
            val preview = storage.preview(source)
            assertTrue(preview.width <= 1024)
            assertEquals(2, preview.width / preview.height)
            preview.recycle()
            val portrait = imported()
            storage.preview(portrait).also {
                assertTrue(it.height > it.width)
                it.recycle()
            }
        }

    @Test
    fun invalidPickerContentIsRejectedAndOwnedCopyCleaned() =
        runBlocking {
            val source = storage.prepareCamera()
            File(context.cacheDir, "photo_drafts/camera/${source.value}").writeText("not an image")
            try {
                storage.importPhoto(storage.cameraUri(source))
                fail("Invalid import accepted")
            } catch (_: java.io.IOException) {
            }
            assertEquals(0, File(context.cacheDir, "photo_drafts").listFiles()!!.count { it.isFile })
            storage.discard(source)
        }
}
