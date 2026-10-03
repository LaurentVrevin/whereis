package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CardinalDirection
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat
import java.util.Date

@RunWith(AndroidJUnit4::class)
class PinDetailScreenTest {
    @get:Rule val compose = createComposeRule()
    private val pin =
        Pin(
            PinId("screen"), GeoPoint(12.5, 24.5), SystemCategoryIds.CAR,
            7f, 42.0, 1_700_000_000_000L, 1_800_000_000_000L, "Mon lieu", "Ma note", true,
        )
    private var navigate = 0
    private var edit = 0
    private var delete = 0
    private var confirm = 0
    private var cancel = 0

    private fun show(
        state: PinDetailUiState = PinDetailUiState.Content(pin),
        dark: Boolean = false,
        large: Boolean = false,
    ) {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                WherisTheme(darkTheme = dark) {
                    PinDetailScreen(
                        state,
                        {},
                        { edit++ },
                        { navigate++ },
                        { delete++ },
                        { confirm++ },
                        { cancel++ },
                        photoContent = {
                            Text(
                                "Photo synthétique",
                                androidx.compose.ui.Modifier.semantics { contentDescription = "Photo du lieu" },
                            )
                        },
                    )
                }
            }
        }
    }

    private fun scroll(text: String) {
        compose.onNodeWithTag("detail_fields").performScrollToNode(hasText(text, substring = true))
    }

    @Test fun namedIdentityCategoryFavoriteAndCreatedDateAreReadable() {
        show()
        compose.onNodeWithText("Mon lieu").assertIsDisplayed()
        compose.onNodeWithText("Catégorie : Voiture").assertIsDisplayed()
        compose.onNodeWithTag("detail_category_icon").assertIsDisplayed()
        scroll("Lieu favori")
        compose.onNodeWithText("Lieu favori").assertIsDisplayed()
        val created = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(pin.createdAtEpochMillis))
        scroll("Enregistré le")
        compose.onNodeWithText("Enregistré le $created").assertIsDisplayed()
    }

    @Test fun minimalPlaceUsesCategoryIdentityAndOmitsOptionalData() {
        show(PinDetailUiState.Content(pin.copy(name = null, note = null, isFavorite = false, accuracyMeters = null, altitudeMeters = null)))
        compose.onNodeWithText("Voiture").assertIsDisplayed()
        compose.onNodeWithText("Catégorie : Voiture").assertIsDisplayed()
        scroll("Lieu non favori")
        compose.onNodeWithText("Lieu non favori").assertIsDisplayed()
        scroll("Position enregistrée")
        compose.onNodeWithText("Précision").assertDoesNotExist()
        compose.onNodeWithText("Altitude").assertDoesNotExist()
        scroll("Supprimer")
        compose.onNodeWithText("Note").assertDoesNotExist()
        compose.onNodeWithText("Depuis ta position actuelle").assertDoesNotExist()
    }

    @Test fun geographicMetadataAndNoteAreDisplayed() {
        show()
        scroll("Coordonnées")
        compose.onNodeWithText("Coordonnées").assertIsDisplayed()
        compose.onNode(hasText("12", substring = true) and hasText("24", substring = true)).assertIsDisplayed()
        scroll("Altitude")
        compose.onNodeWithText("Précision").assertIsDisplayed()
        compose.onNodeWithText("±7 m").assertIsDisplayed()
        compose.onNodeWithText("Altitude").assertIsDisplayed()
        compose.onNodeWithText("42 m").assertIsDisplayed()
        scroll("Ma note")
        compose.onNodeWithText("Ma note").assertIsDisplayed()
    }

    @Test fun localDistanceAndDirectionAppearOnlyWhenAvailable() {
        show(PinDetailUiState.Content(pin, distanceMeters = 150.0, cardinalDirection = CardinalDirection.NORTH_EAST))
        scroll("Depuis ta position actuelle")
        scroll("Direction")
        compose.onNodeWithText("150 m").assertIsDisplayed()
        compose.onNodeWithText("Nord-Est").assertIsDisplayed()
    }

    @Test fun photoSlotAndMapFallbackPreserveAllActions() {
        show(PinDetailUiState.Content(pin.copy(photoReference = PhotoReference("preview"))))
        scroll("Carte indisponible")
        compose.onNodeWithText("Carte indisponible", substring = true).assertIsDisplayed()
        scroll("Photo synthétique")
        compose.onNodeWithContentDescription("Photo du lieu").assertIsDisplayed()
        scroll("Naviguer")
        compose.onNodeWithText("Naviguer").performClick()
        scroll("Modifier")
        compose.onNodeWithText("Modifier").performClick()
        scroll("Supprimer")
        compose.onNodeWithText("Supprimer").performClick()
        assertEquals(1, navigate)
        assertEquals(1, edit)
        assertEquals(1, delete)
    }

    @Test fun liveScreenUpdateReplacesPhotoAndFavoriteWithoutStaleContent() {
        val state = mutableStateOf(pin.copy(photoReference = PhotoReference("a")))
        compose.setContent {
            WherisTheme {
                PinDetailScreen(
                    PinDetailUiState.Content(state.value),
                    {},
                    {},
                    {},
                    {},
                    {},
                    {},
                    photoContent = { Text("Photo ${it.value}") },
                )
            }
        }
        scroll("Photo a")
        compose.onNodeWithText("Photo a").assertIsDisplayed()
        compose.runOnIdle { state.value = pin.copy(name = "Modifié", isFavorite = false, photoReference = PhotoReference("b")) }
        scroll("Photo b")
        compose.onNodeWithText("Photo b").assertIsDisplayed()
        compose.onNodeWithText("Photo a").assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(photoReference = null) }
        scroll("Lieu non favori")
        compose.onNodeWithText("Lieu non favori").assertIsDisplayed()
        compose.onNodeWithText("Photo b").assertDoesNotExist()
    }

    @Test fun confirmationOffersExplicitCancelAndDestructiveSubmit() {
        show(PinDetailUiState.Content(pin, deletion = PinDeletionState.Confirmation))
        compose.onNodeWithText("Supprimer ce lieu ?").assertIsDisplayed()
        compose.onNodeWithText("Annuler").performClick()
        compose.onAllNodesWithText("Supprimer").filterToOne(isEnabled()).performClick()
        assertEquals(1, cancel)
        assertEquals(1, confirm)
    }

    @Test fun inProgressDeletionDisablesSubmitCancelAndBack() {
        show(PinDetailUiState.Content(pin, deletion = PinDeletionState.InProgress))
        compose.onNodeWithText("Suppression en cours…").assertIsDisplayed()
        compose.onNodeWithText("Annuler").assertIsNotEnabled()
        compose.onAllNodesWithText("Supprimer").assertAll(isNotEnabled())
    }

    @Test fun deletionErrorAnnouncedAndRetryCallbackWorks() {
        show(PinDetailUiState.Content(pin, deletion = PinDeletionState.Failed))
        compose.onNodeWithText("Impossible de supprimer ce lieu. Réessaie ou annule.").assertIsDisplayed()
        compose.onAllNodesWithText("Supprimer").filterToOne(isEnabled()).performClick()
        assertEquals(1, confirm)
    }

    @Test fun cleanupFailureDoesNotShowDeletedPinAndAllowsRetry() {
        show(PinDetailUiState.CleanupFailed())
        compose.onNodeWithText("Mon lieu").assertDoesNotExist()
        compose.onNodeWithText("Le lieu a été supprimé", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Réessayer").performClick()
        assertEquals(1, confirm)
    }

    @Test fun darkLargeFontLongNoteKeepsActionsReachable() {
        show(PinDetailUiState.Content(pin.copy(note = "Une note longue. ".repeat(100))), dark = true, large = true)
        scroll("Naviguer")
        compose.onNodeWithText("Naviguer").performClick()
        scroll("Modifier")
        compose.onNodeWithText("Modifier").performClick()
        scroll("Supprimer")
        compose.onNodeWithText("Supprimer").performClick()
        assertEquals(1, navigate)
        assertEquals(1, edit)
        assertEquals(1, delete)
    }

    @Test fun externallyDeletedPlaceHasNoContent() {
        show(PinDetailUiState.NotFound)
        compose.onNodeWithText("Lieu introuvable").assertIsDisplayed()
        compose.onNodeWithText("Mon lieu").assertDoesNotExist()
    }
}
