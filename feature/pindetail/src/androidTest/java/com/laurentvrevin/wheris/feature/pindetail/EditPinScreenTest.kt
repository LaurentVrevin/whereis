package com.laurentvrevin.wheris.feature.pindetail

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.Density
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.PinUpdateResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPinScreenTest {
    @get:Rule val compose = createComposeRule()
    private val custom = Category(CategoryId("custom"), false, "Mon camping", CategoryIconKey.PLACE, CategoryColorKey.ORANGE, 1000L)
    private val initial =
        EditPinUiState.Content(
            PinId("edit"),
            "",
            "",
            listOf(Category(SystemCategoryIds.PARKING, true), Category(SystemCategoryIds.OTHER, true), custom),
            SystemCategoryIds.PARKING,
            false,
            null,
        )
    private val screen = mutableStateOf<EditPinUiState>(initial)
    private var picks = 0
    private var captures = 0
    private var removals = 0
    private var saves = 0
    private var backs = 0
    private var retries = 0
    private var reloads = 0

    private fun change(block: (EditPinUiState.Content) -> EditPinUiState.Content) {
        screen.value = block(screen.value as EditPinUiState.Content)
    }

    private fun render(
        state: EditPinUiState = initial,
        dark: Boolean = false,
        scale: Float = 1f,
    ) {
        screen.value = state
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, scale)) {
                WherisTheme(darkTheme = dark) {
                    EditPinScreen(
                        screen.value, { name -> change { it.copy(name = name) } }, { note -> change { it.copy(note = note) } },
                        { id -> change { it.copy(selectedCategoryId = id) } }, { favorite -> change { it.copy(isFavorite = favorite) } },
                        { picks++ }, { captures++ }, { removals++ }, { saves++ }, { backs++ }, { retries++ }, { reloads++ },
                        photoPreview = {
                            val replacement = (screen.value as? EditPinUiState.Content)?.photo is EditPhotoState.Replacement
                            val description = if (replacement) "Nouvelle photo du lieu" else "Photo actuelle du lieu"
                            Text(description, Modifier.semantics { contentDescription = description })
                        },
                    )
                }
            }
        }
    }

    private fun scroll(text: String) {
        compose.onNodeWithTag("edit_fields").performScrollToNode(hasText(text))
    }

    @Test fun nameAndNoteRetainRawUnicodeAndMultilineText() {
        render()
        compose.onNodeWithText("Nom (facultatif)").performTextInput("  Café 東京  ")
        scroll("Note (facultative)")
        compose.onNodeWithText("Note (facultative)").performTextInput("Ligne 1\n  🌲 ligne 2  ")
        assertEquals("  Café 東京  ", (screen.value as EditPinUiState.Content).name)
        assertEquals("Ligne 1\n  🌲 ligne 2  ", (screen.value as EditPinUiState.Content).note)
    }

    @Test fun dynamicCategorySelectionUsesStableIdentityAndSelectedSemantics() {
        render()
        compose.onNodeWithTag("edit_category_parking").assertIsSelected()
        compose.onNodeWithTag("edit_categories").performScrollToNode(hasTestTag("edit_category_custom"))
        compose.onNodeWithTag("edit_category_custom").performClick().assertIsSelected()
        assertEquals(custom.id, (screen.value as EditPinUiState.Content).selectedCategoryId)
    }

    @Test fun favoriteAnnouncesCheckedAndUnchecked() {
        render()
        scroll("Favori")
        compose.onNodeWithText("Favori").assertIsOff().performClick().assertIsOn().performClick().assertIsOff()
    }

    @Test fun bothPhotoActionsAreAvailableWithoutPhoto() {
        render()
        scroll("Choisir une photo")
        compose.onNodeWithText("Choisir une photo").performClick()
        scroll("Prendre une photo")
        compose.onNodeWithText("Prendre une photo").performClick()
        assertEquals(1, picks)
        assertEquals(1, captures)
    }

    @Test fun originalPreviewHasDescriptionAndExplicitRemoval() {
        render(initial.copy(originalPhoto = PhotoReference("original")))
        scroll("Photo actuelle du lieu")
        compose.onNodeWithContentDescription("Photo actuelle du lieu").assertIsDisplayed()
        scroll("Supprimer la photo")
        compose.onNodeWithText("Supprimer la photo").performClick()
        assertEquals(1, removals)
    }

    @Test fun replacementPreviewAndRemovalAreDistinctFromOriginal() {
        render(initial.copy(originalPhoto = PhotoReference("original"), photo = EditPhotoState.Replacement(PhotoDraftReference("new"))))
        scroll("Nouvelle photo du lieu")
        compose.onNodeWithContentDescription("Nouvelle photo du lieu").assertIsDisplayed()
        scroll("Retirer la nouvelle photo")
        compose.onNodeWithText("Retirer la nouvelle photo").performClick()
        assertEquals(1, removals)
    }

    @Test fun saveAndCancelUseExplicitCallbacks() {
        render()
        compose.onNodeWithText("Enregistrer").assertIsEnabled().performClick()
        compose.onNodeWithText("Annuler").performClick()
        assertEquals(1, saves)
        assertEquals(1, backs)
    }

    @Test fun loadingAndFailedLoadOfferBackAndRetry() {
        render(EditPinUiState.Loading)
        compose.onNodeWithTag("edit_loading").assertIsDisplayed()
        compose.runOnIdle { screen.value = EditPinUiState.LoadFailed }
        compose.onNodeWithText("Réessayer").performClick()
        assertEquals(1, retries)
        compose.onNodeWithText("Annuler").assertIsEnabled()
    }

    @Test fun unavailableCategoryDisablesSaveAndOffersReload() {
        render(initial.copy(selectedCategoryId = CategoryId("missing"), saveError = PinUpdateResult.CATEGORY_NOT_FOUND))
        compose.onNodeWithText("Enregistrer").assertIsNotEnabled()
        scroll("Actualiser les catégories")
        compose.onNodeWithText("Actualiser les catégories").performClick()
        assertEquals(1, reloads)
    }

    @Test fun photoAndTechnicalErrorsKeepFieldsAndRetryAvailable() {
        render(initial.copy(name = "Draft", photoFailed = true, saveError = PinUpdateResult.TECHNICAL_FAILURE))
        scroll("Cette photo ne peut pas être affichée ou utilisée. Tu peux la retirer ou choisir une autre photo.")
        compose.onNodeWithText(
            "Cette photo ne peut pas être affichée ou utilisée. Tu peux la retirer ou choisir une autre photo.",
        ).assertIsDisplayed()
        scroll("Impossible d’enregistrer les modifications. Tes changements sont conservés, tu peux réessayer.")
        compose.onNodeWithText("Enregistrer").assertIsEnabled()
        compose.onNodeWithText("Annuler").assertIsEnabled()
    }

    @Test fun savingAndAcquisitionPreventDoubleSubmission() {
        render(initial.copy(isSaving = true))
        compose.onNodeWithText("Enregistrement en cours…").assertIsNotEnabled()
        compose.onNodeWithText("Annuler").assertIsNotEnabled()
        compose.runOnIdle { screen.value = initial.copy(isAcquiringPhoto = true) }
        compose.onNodeWithText("Enregistrer").assertIsNotEnabled()
        scroll("Choisir une photo")
        compose.onNodeWithText("Choisir une photo").assertIsNotEnabled()
    }

    @Test fun largeFontDarkFormScrollsWithKeyboardAndKeepsActionsReachable() {
        render(initial.copy(note = "Une note longue\n".repeat(18)), dark = true, scale = 1.5f)
        compose.onNodeWithText("Nom (facultatif)").performTextInput("Nom")
        scroll("Favori")
        compose.onNodeWithText("Favori").assertIsDisplayed()
        scroll("Prendre une photo")
        compose.onNodeWithText("Prendre une photo").assertIsDisplayed()
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Enregistrer").assertIsDisplayed().performClick()
        assertEquals(1, saves)
    }
}
