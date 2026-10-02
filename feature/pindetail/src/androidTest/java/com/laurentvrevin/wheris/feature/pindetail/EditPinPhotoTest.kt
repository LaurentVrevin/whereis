package com.laurentvrevin.wheris.feature.pindetail

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.data.mapper.toDomain
import com.laurentvrevin.wheris.data.repository.CategoryRepositoryImpl
import com.laurentvrevin.wheris.data.repository.PinRepositoryImpl
import com.laurentvrevin.wheris.domain.PhotoStorage
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class EditPinPhotoTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var db: WherisDatabase
    private lateinit var storage: AndroidPhotoStorage
    private lateinit var repository: PinRepositoryImpl
    private lateinit var vm: EditPinViewModel
    private val store = ViewModelStore()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var failUpdate = false
    private var promotions = 0

    @Before fun setUp() {
        clean()
        db =
            Room.inMemoryDatabaseBuilder(context, WherisDatabase::class.java)
                .addCallback(WherisDatabase.getCallback()).build()
        storage = AndroidPhotoStorage(context)
        repository = PinRepositoryImpl(db.pinDao(), storage)
    }

    @After fun tearDown() {
        main { store.clear() }
        runBlocking { delay(100) }
        scope.cancel()
        db.close()
        clean()
    }

    private fun main(action: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(action)

    private fun clean() {
        listOf(File(context.cacheDir, "photo_drafts"), File(context.filesDir, "photos"), File(context.filesDir, "photo_pending_delete"))
            .forEach { it.deleteRecursively() }
        File(context.cacheDir, "edit-fixture.png").delete()
    }

    private suspend fun draft(): PhotoDraftReference {
        val fixture = File(context.cacheDir, "edit-fixture.png")
        Bitmap.createBitmap(40, 60, Bitmap.Config.ARGB_8888).apply {
            fixture.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
        return storage.importPhoto(Uri.fromFile(fixture))
    }

    private suspend fun start(withPhoto: Boolean = true): Pin {
        val original =
            CreatePinUseCase(repository, { PinId("edit") }, { 2000L })(
                UserLocation(GeoPoint(10.0, 20.0), 7f, 30.0, 1000L),
                SystemCategoryIds.CAR,
                "Original",
                "Note",
                photoReference = if (withPhoto) storage.promote(draft()) else null,
            )
        val updateRepository =
            object : PinRepository by repository {
                override suspend fun updatePin(
                    update: PinUpdate,
                    updatedAtEpochMillis: Long,
                ): PinUpdateResult {
                    if (failUpdate) return PinUpdateResult.TECHNICAL_FAILURE
                    return repository.updatePin(update, updatedAtEpochMillis)
                }
            }
        val trackedStorage =
            object : PhotoStorage by storage {
                override suspend fun promote(draft: PhotoDraftReference): PhotoReference {
                    promotions++
                    return storage.promote(draft)
                }
            }
        main {
            vm =
                EditPinViewModel(
                    repository, CategoryRepositoryImpl(db.categoryDao()), UpdatePinUseCase(updateRepository) { 5000L },
                    trackedStorage, SavedStateHandle(), scope,
                )
            store.put("editor", vm)
            vm.load(original.id)
        }
        await { vm.uiState.value is EditPinUiState.Content }
        return original
    }

    private suspend fun await(condition: () -> Boolean) = withTimeout(5000) { while (!condition()) delay(10) }

    private suspend fun replace(): PhotoDraftReference {
        val draft = draft()
        main {
            assertTrue(vm.beginPhotoAcquisition())
            vm.photoReady(draft)
        }
        return draft
    }

    private fun permanent(photo: PhotoReference) = File(context.filesDir, "photos/${photo.value}")

    private fun temporary(draft: PhotoDraftReference) = File(context.cacheDir, "photo_drafts/${draft.value}")

    private suspend fun row(pin: Pin) = db.pinDao().getPin(pin.id.value)?.toDomain()

    @Test fun persistedPhotoSurvivesCancelWithoutEdits() =
        runBlocking {
            val pin = start()
            val bytes = permanent(pin.photoReference!!).readBytes()
            main { vm.abandon() }
            assertEquals(pin, row(pin))
            assertArrayEquals(bytes, permanent(pin.photoReference!!).readBytes())
        }

    @Test fun removalIntentionThenCancelLeavesOriginalUntouched() =
        runBlocking {
            val pin = start()
            val bytes = permanent(pin.photoReference!!).readBytes()
            main {
                vm.removePhoto()
                vm.abandon()
            }
            assertEquals(pin, row(pin))
            assertArrayEquals(bytes, permanent(pin.photoReference!!).readBytes())
        }

    @Test fun replacementThenCancelCleansOnlyNewDraft() =
        runBlocking {
            val pin = start()
            val draft = replace()
            assertTrue(temporary(draft).exists())
            main { vm.abandon() }
            await { !temporary(draft).exists() }
            assertEquals(pin, row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun promotedReplacementAfterUpdateFailureCanPreviewAndRetryWithoutAnotherPromotion() =
        runBlocking {
            val pin = start()
            val draft = replace()
            failUpdate = true
            main { vm.save() }
            await { (vm.uiState.value as? EditPinUiState.Content)?.saveError == PinUpdateResult.TECHNICAL_FAILURE }
            assertEquals(pin, row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
            storage.preview(draft).recycle()
            assertTrue(permanent(PhotoReference(draft.value)).exists())
            failUpdate = false
            main { vm.save() }
            await { vm.uiState.value == EditPinUiState.Saved }
            assertEquals(PhotoReference(draft.value), row(pin)!!.photoReference)
            assertEquals(1, promotions)
            storage.discard(draft)
            assertTrue(permanent(PhotoReference(draft.value)).exists())
            assertFalse(permanent(pin.photoReference!!).exists())
        }

    @Test fun replacementSuccessAttachesNewAndCleansOld() =
        runBlocking {
            val pin = start()
            val draft = replace()
            main { vm.save() }
            await { vm.uiState.value == EditPinUiState.Saved }
            assertEquals(PhotoReference(draft.value), row(pin)!!.photoReference)
            assertFalse(permanent(pin.photoReference!!).exists())
            storage.discard(draft)
            assertTrue(permanent(PhotoReference(draft.value)).exists())
        }

    @Test fun addingPhotoToPhotoLessPlaceAttachesIt() =
        runBlocking {
            val pin = start(withPhoto = false)
            val draft = replace()
            main { vm.save() }
            await { vm.uiState.value == EditPinUiState.Saved }
            assertEquals(PhotoReference(draft.value), row(pin)!!.photoReference)
            storage.discard(draft)
            assertTrue(permanent(PhotoReference(draft.value)).exists())
        }

    @Test fun cancellingPromotedFailedReplacementCleansNewPermanentAndKeepsOriginal() =
        runBlocking {
            val pin = start()
            val draft = replace()
            failUpdate = true
            main { vm.save() }
            await { (vm.uiState.value as? EditPinUiState.Content)?.saveError == PinUpdateResult.TECHNICAL_FAILURE }
            main { vm.abandon() }
            await { !permanent(PhotoReference(draft.value)).exists() }
            assertEquals(pin, row(pin))
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun routePreviewsRealPermanentPhotoAndFakeLaunchersCancelWithoutChangingIt() =
        runBlocking {
            val pin = start()
            var picks = 0
            var takes = 0
            compose.setContent {
                WherisTheme {
                    EditPinRoute(
                        pin.id,
                        {},
                        viewModel = vm,
                        storage = storage,
                        photoScope = scope,
                        photoLaunchers =
                            EditPhotoLaunchers({
                                picks++
                                vm.photoCancelled()
                            }, {
                                takes++
                                vm.photoCancelled()
                            }),
                    )
                }
            }
            compose.onNodeWithTag("edit_fields").performScrollToNode(hasText("Photo (facultative)"))
            compose.waitUntil(5000) {
                compose.onAllNodes(androidx.compose.ui.test.hasContentDescription("Photo actuelle du lieu"))
                    .fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("Photo actuelle du lieu").assertIsDisplayed()
            compose.onNodeWithTag("edit_fields").performScrollToNode(hasText("Choisir une photo"))
            compose.onNodeWithText("Choisir une photo").performClick()
            compose.onNodeWithTag("edit_fields").performScrollToNode(hasText("Prendre une photo"))
            compose.onNodeWithText("Prendre une photo").performClick()
            assertEquals(1, picks)
            assertEquals(1, takes)
            assertEquals(pin, row(pin))
        }

    @Test fun missingPermanentPreviewDegradesWithoutChangingDatabaseAndStillAllowsRemoval() =
        runBlocking {
            val pin = start()
            permanent(pin.photoReference!!).delete()
            compose.setContent { WherisTheme { EditPinRoute(pin.id, {}, viewModel = vm, storage = storage, photoScope = scope) } }
            compose.onNodeWithTag("edit_fields").performScrollToNode(hasText("Photo (facultative)"))
            await { (vm.uiState.value as? EditPinUiState.Content)?.photoFailed == true }
            assertEquals(pin, row(pin))
            main { vm.removePhoto() }
            assertEquals(EditPhotoState.Removed, (vm.uiState.value as EditPinUiState.Content).photo)
        }
}
