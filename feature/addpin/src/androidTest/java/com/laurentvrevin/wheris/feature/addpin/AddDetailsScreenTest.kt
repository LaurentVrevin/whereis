package com.laurentvrevin.wheris.feature.addpin

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoDraftReference
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddDetailsScreenTest {
    @get:Rule val compose = createComposeRule()
    private val selection =
        AddPinUiState.CategorySelection(
            UserLocation(GeoPoint(12.0, 24.0), timestampEpochMillis = 1000L),
            categories = listOf(Category(SystemCategoryIds.PARKING, true)),
            selectedCategoryId = SystemCategoryIds.PARKING,
            isLoadingCategories = false,
        )
    private val screen = mutableStateOf<AddPinUiState>(AddPinUiState.Details(selection))
    private var saves = 0
    private var picks = 0
    private var captures = 0

    private fun change(transform: (PlaceDetailsDraft) -> PlaceDetailsDraft) {
        val current = screen.value as AddPinUiState.Details
        screen.value = current.copy(selection = current.selection.copy(details = transform(current.selection.details)))
    }

    private fun render(
        state: AddPinUiState = AddPinUiState.Details(selection),
        dark: Boolean = false,
        fontScale: Float = 1f,
    ) {
        screen.value = state
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                WherisTheme(darkTheme = dark) {
                    AddPinScreen(
                        screen.value, {}, {}, {}, {}, {}, {}, { saves++ }, {}, {},
                        onOpenDetails = { screen.value = AddPinUiState.Details(selection) },
                        onBackFromDetails = { screen.value = (screen.value as AddPinUiState.Details).selection },
                        onNameChange = { text -> change { it.copy(name = text) } },
                        onNoteChange = { text -> change { it.copy(note = text) } },
                        onFavoriteChange = { checked -> change { it.copy(isFavorite = checked) } },
                        onChoosePhoto = { picks++ },
                        onTakePhoto = { captures++ },
                        onRemovePhoto = { change { it.copy(photo = null) } },
                        photoPreview = { Text("Photo du lieu") },
                    )
                }
            }
        }
    }

    @Test
    fun selectedCategoryOffersDetailsAndEmptyFormCanSave() {
        render(selection)
        compose.onNodeWithText("Ajouter des détails").assertIsEnabled().performClick()
        compose.onNodeWithText("Nom (facultatif)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Note (facultative)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Enregistrer").assertIsEnabled().performClick()
        assertEquals(1, saves)
    }

    @Test
    fun bothSystemActionsAreExplicitAndCallable() {
        render()
        compose.onNodeWithText("Choisir une photo").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Prendre une photo").performScrollTo().assertIsDisplayed().performClick()
        assertEquals(1, picks)
        assertEquals(1, captures)
    }

    @Test
    fun optionalTextInputsPreserveUnicodeAndMultilineFormatting() {
        render()
        compose.onNodeWithText("Nom (facultatif)").performScrollTo().performTextInput("  Café 東京  ")
        compose.onNodeWithText("Note (facultative)").performScrollTo().performTextInput("Première ligne\n  🌲 seconde ligne  ")
        val details = (screen.value as AddPinUiState.Details).selection.details
        assertEquals("  Café 東京  ", details.name)
        assertEquals("Première ligne\n  🌲 seconde ligne  ", details.note)
        compose.onNodeWithText("Enregistrer").assertIsEnabled()
    }

    @Test
    fun favoriteExposesCheckedStateAndFieldsRemainOptional() {
        render()
        compose.onNodeWithText("Favori (facultatif)").performScrollTo().assertIsOff().performClick().assertIsOn()
        compose.onNodeWithText("Enregistrer").assertIsEnabled()
    }

    @Test
    fun previewAndRemoveDoNotEraseOtherDetails() {
        val details = PlaceDetailsDraft("Nom", "Note", true, PhotoDraftReference("fixture"))
        render(AddPinUiState.Details(selection.copy(details = details)))
        compose.onNodeWithText("Photo du lieu").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Supprimer la photo").performScrollTo().performClick()
        assertEquals(details.copy(photo = null), (screen.value as AddPinUiState.Details).selection.details)
        compose.onNodeWithText("Choisir une photo").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun photoFailureOffersRetryAndSavingWithoutPhoto() {
        render(AddPinUiState.Details(selection.copy(details = PlaceDetailsDraft(photoFailed = true))))
        compose.onNodeWithText("Impossible d’ajouter cette photo. Tu peux réessayer ou continuer sans photo.")
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Prendre une photo").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Enregistrer").assertIsEnabled()
    }

    @Test
    fun backReturnsCanonicalFieldsToCategorySelection() {
        val details = PlaceDetailsDraft("Nom", "Note", true)
        render(AddPinUiState.Details(selection.copy(details = details)))
        compose.onNodeWithText("Retour aux catégories").performClick()
        assertEquals(details, (screen.value as AddPinUiState.CategorySelection).details)
        compose.onNodeWithText("Enregistrer").assertIsEnabled()
    }

    @Test
    fun savingDisablesSubmissionBackAndPhotoActions() {
        render(AddPinUiState.Details(selection.copy(isSaving = true)))
        compose.onNodeWithText("Enregistrement…").assertIsNotEnabled()
        compose.onNodeWithText("Retour aux catégories").assertIsNotEnabled()
        compose.onNodeWithText("Choisir une photo").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun failedSaveCanRetryWithLongNoteDarkThemeAndLargeFont() {
        render(
            AddPinUiState.Details(selection.copy(saveFailed = true, details = PlaceDetailsDraft(note = "Une longue note\n".repeat(40)))),
            dark = true,
            fontScale = 1.5f,
        )
        compose.onNodeWithText("Note (facultative)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("details_fields").performScrollToNode(hasText("Favori (facultatif)"))
        compose.onNodeWithText("Favori (facultatif)").assertIsDisplayed()
        compose.onNodeWithTag("details_fields").performScrollToNode(
            hasText("Le lieu n’a pas pu être enregistré. Tes informations sont conservées. Réessaie."),
        )
        compose.onNodeWithText("Le lieu n’a pas pu être enregistré. Tes informations sont conservées. Réessaie.")
            .assertIsDisplayed()
        compose.onNodeWithText("Enregistrer").assertIsEnabled().performClick()
        assertEquals(1, saves)
    }
}
