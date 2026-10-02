package com.laurentvrevin.wheris.core.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.domain.PhotoStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.UUID

/** Only this Android boundary handles external URIs and private physical paths. */
class AndroidPhotoStorage(
    context: Context,
    private val deleteFile: (File) -> Boolean = { it.delete() },
) : PhotoStorage {
    private val context = context.applicationContext
    private val drafts = File(this.context.cacheDir, "photo_drafts")
    private val camera = File(drafts, "camera")
    private val permanent = File(this.context.filesDir, "photos")
    private val pending = File(this.context.filesDir, "photo_pending_delete")
    private val mutex = Mutex()

    // A lease protects a current acquisition or promoted-pending-save against reconciliation.
    // Leases intentionally disappear on process death; Room then determines ownership.
    private val active = mutableSetOf<String>()

    suspend fun importPhoto(uri: Uri): PhotoDraftReference =
        io {
            val draft = createDraft(cameraTarget = false)
            try {
                context.contentResolver.openInputStream(uri)?.use { source ->
                    draftFile(draft).outputStream().use { target ->
                        source.copyTo(target)
                        target.fd.sync()
                    }
                } ?: throw IOException("Photo source unavailable")
                decode(draftFile(draft)).recycle()
                draft
            } catch (exception: Exception) {
                try {
                    remove(draftFile(draft))
                } finally {
                    active.remove(draft.value)
                }
                throw exception
            }
        }

    suspend fun prepareCamera(): PhotoDraftReference = io { createDraft(cameraTarget = true) }

    fun cameraUri(draft: PhotoDraftReference): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.photo-drafts", File(camera, draft.value))

    suspend fun validateCamera(draft: PhotoDraftReference): PhotoDraftReference =
        io {
            try {
                check(draft.value in active)
                decode(draftFile(draft)).recycle()
                draft
            } catch (exception: Exception) {
                try {
                    remove(draftFile(draft))
                } finally {
                    active.remove(draft.value)
                }
                throw exception
            }
        }

    suspend fun preview(draft: PhotoDraftReference): Bitmap =
        io {
            decode(
                draftFile(draft).takeIf { it.exists() }
                    ?: File(permanent, draft.value).takeIf { it.exists() }
                    ?: File(pending, draft.value),
            )
        }

    /** Reads an attached photo without manufacturing a draft or acquiring a lease. */
    suspend fun preview(photo: PhotoReference): Bitmap = io { decode(File(permanent, photo.value)) }

    override suspend fun promote(draft: PhotoDraftReference): PhotoReference =
        io {
            check(draft.value in active)
            val target = File(permanent, draft.value)
            if (!target.exists()) {
                val source = draftFile(draft).takeIf { it.exists() } ?: File(pending, draft.value)
                decode(source).recycle()
                directory(permanent)
                Files.move(source.toPath(), target.toPath())
            } else {
                decode(target).recycle()
            }
            PhotoReference(draft.value)
        }

    override suspend fun discard(draft: PhotoDraftReference) =
        io {
            // Never remove an attached permanent photo, even if a late acquisition callback arrives.
            if (draft.value in active) {
                val photo = File(permanent, draft.value)
                if (photo.exists()) {
                    directory(pending)
                    Files.move(photo.toPath(), File(pending, draft.value).toPath())
                }
                remove(draftFile(draft))
                remove(File(pending, draft.value))
                // Retain the lease on failure: removal can retry, preview can read pending bytes,
                // and promotion can restore those bytes before any database insertion.
                active.remove(draft.value)
            }
        }

    override suspend fun abandon(draft: PhotoDraftReference) {
        try {
            discard(draft)
        } finally {
            io { active.remove(draft.value) }
        }
    }

    override suspend fun attached(photo: PhotoReference) =
        io {
            active.remove(photo.value)
            Unit
        }

    override suspend fun verifyPermanent(photo: PhotoReference) = io { decode(File(permanent, photo.value)).recycle() }

    override suspend fun stageDeletion(photo: PhotoReference) =
        io {
            val source = File(permanent, photo.value)
            val target = File(pending, photo.value)
            if (source.exists()) {
                directory(pending)
                check(!target.exists())
                Files.move(source.toPath(), target.toPath())
            }
        }

    override suspend fun restoreDeletion(photo: PhotoReference) =
        io {
            val source = File(pending, photo.value)
            if (source.exists()) {
                directory(permanent)
                check(!File(permanent, photo.value).exists())
                Files.move(source.toPath(), File(permanent, photo.value).toPath())
            }
        }

    override suspend fun finishDeletion(photo: PhotoReference) = io { remove(File(pending, photo.value)) }

    override suspend fun reconcile(attached: Set<PhotoReference>) =
        io {
            val referenced = attached.map { it.value }.toSet()
            ownedFiles(pending).forEach { file ->
                if (file.name in referenced) {
                    directory(permanent)
                    val target = File(permanent, file.name)
                    if (target.exists()) {
                        remove(file)
                    } else {
                        Files.move(file.toPath(), target.toPath())
                    }
                } else if (file.name !in active) {
                    remove(file)
                }
            }
            ownedFiles(permanent).filter { it.name !in referenced && it.name !in active }.forEach(::remove)
            (ownedFiles(drafts) + ownedFiles(camera)).filter { it.name !in active }.forEach(::remove)
        }

    private fun createDraft(cameraTarget: Boolean): PhotoDraftReference {
        val directory = if (cameraTarget) camera else drafts
        directory(directory)
        val draft = PhotoDraftReference(UUID.randomUUID().toString())
        check(File(directory, draft.value).createNewFile())
        active.add(draft.value)
        return draft
    }

    private fun draftFile(draft: PhotoDraftReference): File = File(camera, draft.value).takeIf { it.exists() } ?: File(drafts, draft.value)

    private fun directory(file: File) {
        if (!file.isDirectory && !file.mkdirs()) throw IOException("Photo directory unavailable")
    }

    private fun ownedFiles(directory: File): List<File> {
        if (!directory.exists()) return emptyList()
        return (directory.listFiles() ?: throw IOException("Photo directory unreadable"))
            .filter { it.isFile && it.name.matches(Regex("[A-Za-z0-9][A-Za-z0-9_-]*")) }
    }

    private fun remove(file: File) {
        if (file.exists() && !deleteFile(file)) throw IOException("Photo cleanup failed")
    }

    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { mutex.withLock { block() } }

    private fun decode(file: File): Bitmap {
        if (!file.isFile || file.length() == 0L) throw IOException("Empty photo")
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, options)
        if (options.outWidth <= 0 || options.outHeight <= 0) throw IOException("Invalid photo")
        var sample = 1
        while (maxOf(options.outWidth, options.outHeight) / sample > 1024) sample *= 2
        options.inJustDecodeBounds = false
        options.inSampleSize = sample
        val bitmap = BitmapFactory.decodeFile(file.path, options) ?: throw IOException("Invalid photo")
        val orientation =
            runCatching {
                ExifInterface(file).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        }
        if (matrix.isIdentity) return bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also { bitmap.recycle() }
    }
}
