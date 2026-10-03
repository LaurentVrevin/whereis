package com.laurentvrevin.wheris.feature.pindetail

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.navigation.ExternalNavigationResult
import com.laurentvrevin.wheris.core.navigation.ExternalNavigator
import com.laurentvrevin.wheris.core.photo.AndroidPhotoStorage
import com.laurentvrevin.wheris.data.repository.CategoryRepositoryImpl
import com.laurentvrevin.wheris.data.repository.PinRepositoryImpl
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PinDetailPhotoTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var db: WherisDatabase
    private lateinit var storage: AndroidPhotoStorage
    private lateinit var repository: PinRepositoryImpl
    private lateinit var vm: PinDetailViewModel
    private val store = ViewModelStore()
    private var back = 0
    private var edit = 0
    private var navResult = ExternalNavigationResult.Launched
    private var destination: GeoPoint? = null

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
        db.close()
        clean()
    }

    private fun clean() {
        listOf(
            File(context.cacheDir, "photo_drafts"),
            File(context.filesDir, "photos"),
            File(context.filesDir, "photo_pending_delete"),
        ).forEach { it.deleteRecursively() }
        File(context.cacheDir, "detail-fixture.png").delete()
    }

    private fun main(action: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(action)

    private suspend fun await(condition: () -> Boolean) = withTimeout(5000) { while (!condition()) delay(10) }

    private fun permanent(reference: PhotoReference) = File(context.filesDir, "photos/${reference.value}")

    private suspend fun photo(): PhotoReference {
        val fixture = File(context.cacheDir, "detail-fixture.png")
        Bitmap.createBitmap(40, 60, Bitmap.Config.ARGB_8888).apply {
            fixture.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
        return storage.promote(storage.importPhoto(Uri.fromFile(fixture)))
    }

    private suspend fun start(withPhoto: Boolean = true): Pin {
        val pin =
            CreatePinUseCase(repository, { PinId("detail") }, { 2000L })(
                UserLocation(GeoPoint(10.0, 20.0), 7f, 30.0, 1000L),
                SystemCategoryIds.CAR,
                "Original privé",
                "Note privée",
                photoReference = if (withPhoto) photo() else null,
            )
        main {
            vm =
                PinDetailViewModel(
                    repository,
                    object : UserLocationRepository {
                        override suspend fun getCurrentLocation(): LocationResult = LocationResult.Timeout
                    },
                    CategoryRepositoryImpl(db.categoryDao()),
                )
            store.put("detail", vm)
            vm.observePin(pin.id)
        }
        await { vm.uiState.value is PinDetailUiState.Content }
        return pin
    }

    private fun show(pin: Pin) {
        compose.setContent {
            WherisTheme {
                PinDetailRoute(
                    pin.id,
                    { back++ },
                    { edit++ },
                    viewModel = vm,
                    storage = storage,
                    navigator =
                        ExternalNavigator {
                            destination = it
                            navResult
                        },
                    mapContent = { DetailMapUnavailable() },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun scroll(text: String) = compose.onNodeWithTag("detail_fields").performScrollToNode(hasText(text, substring = true))

    private fun awaitPhoto() {
        scroll("Choisir une application externe")
        // Photo is the next lazy item; bring its loading/fallback/image into composition.
        compose.onNodeWithTag("detail_fields").performScrollToIndex(5)
        compose.waitUntil(5000) {
            compose.onAllNodesWithContentDescription("Photo du lieu enregistré").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Photo du lieu enregistré").assertIsDisplayed()
    }

    private fun awaitPhotoFailure() {
        compose.onNodeWithTag("detail_fields").performScrollToIndex(5)
        compose.waitUntil(5000) {
            compose.onAllNodesWithText("Photo indisponible", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test fun realPermanentPhotoIsShownWithoutDraftAndDatabaseUnchanged(): Unit =
        runBlocking {
            val pin = start()
            show(pin)
            awaitPhoto()
            assertEquals(pin, repository.observePin(pin.id).first())
            assertTrue(File(context.cacheDir, "photo_drafts").listFiles().orEmpty().none { it.isFile })
            storage.reconcile(setOf(pin.photoReference!!))
            assertTrue(permanent(pin.photoReference!!).exists())
        }

    @Test fun missingPhotoFallsBackWithoutChangingRowAndEditRemainsAvailable(): Unit =
        runBlocking {
            val pin = start()
            assertTrue(permanent(pin.photoReference!!).delete())
            show(pin)
            awaitPhotoFailure()
            compose.onNodeWithText("Photo indisponible", substring = true).assertIsDisplayed()
            assertEquals(pin, repository.observePin(pin.id).first())
            scroll("Modifier")
            compose.onNodeWithText("Modifier").performClick()
            assertEquals(1, edit)
            scroll("Supprimer")
            compose.onNodeWithText("Supprimer").assertIsEnabled()
        }

    @Test fun corruptPhotoFallsBackWithoutChangingRow(): Unit =
        runBlocking {
            val pin = start()
            permanent(pin.photoReference!!).writeText("corrupt")
            show(pin)
            awaitPhotoFailure()
            compose.onNodeWithText("Photo indisponible", substring = true).assertIsDisplayed()
            assertEquals(pin, repository.observePin(pin.id).first())
            scroll("Supprimer")
            compose.onNodeWithText("Supprimer").assertIsEnabled()
        }

    @Test fun routeDeletionReturnsOnlyAfterRepositoryAndCleansPhoto(): Unit =
        runBlocking {
            val pin = start()
            show(pin)
            scroll("Supprimer")
            compose.onNodeWithText("Supprimer").performClick()
            compose.onAllNodesWithText("Supprimer").filterToOne(isEnabled()).performClick()
            compose.waitUntil(5000) { back == 1 }
            assertNull(repository.observePin(pin.id).first())
            assertFalse(permanent(pin.photoReference!!).exists())
            assertTrue(File(context.filesDir, "photo_pending_delete").listFiles().orEmpty().isEmpty())
            assertEquals(PinDetailUiState.Deleted, vm.uiState.value)
        }

    @Test fun navigationCallbackPassesCoordinatesOnlyAndSuccessKeepsDetail(): Unit =
        runBlocking {
            val pin = start(false)
            show(pin)
            scroll("Naviguer")
            compose.onNodeWithText("Naviguer").performClick()
            assertEquals(pin.position, destination)
            assertEquals(0, back)
            assertEquals(pin, repository.observePin(pin.id).first())
        }

    @Test fun unavailableNavigationShowsMessageAndCanRetrySuccessfully(): Unit =
        runBlocking {
            val pin = start(false)
            navResult = ExternalNavigationResult.Unavailable
            show(pin)
            scroll("Naviguer")
            compose.onNodeWithText("Naviguer").performClick()
            scroll("Aucune application compatible")
            compose.onNodeWithText("Aucune application compatible", substring = true).assertIsDisplayed()
            navResult = ExternalNavigationResult.Launched
            scroll("Naviguer")
            compose.onNodeWithText("Naviguer").performClick()
            compose.onNodeWithText("Aucune application compatible", substring = true).assertDoesNotExist()
            assertEquals(pin.position, destination)
        }

    @Test fun launchFailureShowsRecoverableMessageWithoutLeavingDetail(): Unit =
        runBlocking {
            val pin = start(false)
            navResult = ExternalNavigationResult.Failed
            show(pin)
            scroll("Naviguer")
            compose.onNodeWithText("Naviguer").performClick()
            scroll("Impossible d’ouvrir")
            compose.onNodeWithText("Impossible d’ouvrir", substring = true).assertIsDisplayed()
            assertEquals(0, back)
            assertEquals(pin, repository.observePin(pin.id).first())
        }

    @Test fun canonicalUpdateRefreshesNameCategoryFavoritePhotoAndRemovalOnOpenDetail(): Unit =
        runBlocking {
            val pin = start()
            show(pin)
            awaitPhoto()
            val replacement = photo()
            UpdatePinUseCase(repository) { 5000L }(
                PinUpdate(
                    pin.id,
                    SystemCategoryIds.OTHER,
                    "Après édition",
                    "Note mise à jour",
                    true,
                    PinPhotoChange.Replace(replacement),
                ),
            )
            await { (vm.uiState.value as? PinDetailUiState.Content)?.pin?.photoReference == replacement }
            scroll("Après édition")
            compose.onNodeWithText("Après édition").assertIsDisplayed()
            compose.onNodeWithText("Catégorie : Autre").assertIsDisplayed()
            scroll("Lieu favori")
            compose.onNodeWithText("Lieu favori").assertIsDisplayed()
            awaitPhoto()
            assertFalse(permanent(pin.photoReference!!).exists())
            assertTrue(permanent(replacement).exists())
            assertEquals(pin.createdAtEpochMillis, (vm.uiState.value as PinDetailUiState.Content).pin.createdAtEpochMillis)
            UpdatePinUseCase(repository) { 6000L }(
                PinUpdate(
                    pin.id,
                    SystemCategoryIds.OTHER,
                    "Après édition",
                    null,
                    false,
                    PinPhotoChange.Remove,
                ),
            )
            await { (vm.uiState.value as? PinDetailUiState.Content)?.pin?.photoReference == null }
            scroll("Lieu non favori")
            compose.onNodeWithText("Lieu non favori").assertIsDisplayed()
            compose.onNodeWithContentDescription("Photo du lieu enregistré").assertDoesNotExist()
            assertFalse(permanent(replacement).exists())
        }
}
