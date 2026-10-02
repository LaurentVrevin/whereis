package com.laurentvrevin.wheris.feature.addpin

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateCategoryFlowTest {
    @get:Rule
    val compose = createComposeRule()
    private val location = UserLocation(GeoPoint(12.0, 24.0), 8f, 35.0, 1000L)
    private val categories = TestCategories()
    private val pins = TestPins()
    private lateinit var viewModel: AddPinViewModel

    @Before
    fun setUp() {
        compose.runOnUiThread {
            viewModel =
                AddPinViewModel(
                    object : UserLocationRepository {
                        override suspend fun getCurrentLocation(): LocationResult = LocationResult.Success(location)
                    },
                    categories,
                    CreatePinUseCase(pins),
                    FakePhotoStorage(),
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main.immediate),
                )
            viewModel.onPermissionGranted(true, true)
        }
        compose.waitUntil { viewModel.uiState.value is AddPinUiState.PositionFound }
        compose.runOnUiThread { viewModel.confirmPosition() }
        compose.waitUntil { (viewModel.uiState.value as? AddPinUiState.CategorySelection)?.isLoadingCategories == false }
        compose.setContent {
            WherisTheme {
                val state = viewModel.uiState.collectAsState().value
                BackHandler(enabled = state is AddPinUiState.CategoryCreation) { viewModel.cancelCategoryCreation() }
                AddPinScreen(
                    uiState = state,
                    onRequestPermission = {},
                    onOpenSettings = {},
                    onOpenLocationSettings = {},
                    onRetryLocation = {},
                    onConfirmPosition = viewModel::confirmPosition,
                    onSelectCategory = viewModel::selectCategory,
                    onSave = viewModel::savePin,
                    onBackToPosition = viewModel::backToPosition,
                    onFinished = {},
                    onOpenCategoryCreation = viewModel::openCategoryCreation,
                    onCategoryNameChange = viewModel::updateCategoryName,
                    onCategoryIconChange = viewModel::selectCategoryIcon,
                    onCategoryColorChange = viewModel::selectCategoryColor,
                    onCreateCategory = viewModel::createCategory,
                    onCancelCategoryCreation = viewModel::cancelCategoryCreation,
                )
            }
        }
    }

    @Test
    fun createUpdatesPreviewReturnsSelectedAndSavesWithCustomId() {
        compose.onNodeWithText("Créer une catégorie").assertIsDisplayed().performClick()
        compose.onNodeWithText("Nouvelle catégorie").assertIsDisplayed()
        compose.onNodeWithTag("create_category").assertIsNotEnabled()
        compose.onNodeWithTag("category_name").performTextInput("  茸 — Champignons  ")
        compose.onNodeWithTag("icon_park").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("color_PURPLE").performScrollTo().performClick().assertIsSelected()
        compose.onNode(hasText("茸 — Champignons") and hasAnyAncestor(hasTestTag("category_preview")))
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("create_category").performClick()
        compose.onNodeWithText("茸 — Champignons").assertIsDisplayed().assertIsSelected()
        compose.runOnIdle {
            assertEquals(location, (viewModel.uiState.value as AddPinUiState.CategorySelection).location)
            assertEquals(CategoryIconKey.PARK, categories.created?.iconKey)
            assertEquals(CategoryColorKey.PURPLE, categories.created?.colorKey)
        }
        compose.onNodeWithText("Enregistrer").performClick()
        compose.onNodeWithText("Lieu enregistré !").assertIsDisplayed()
        compose.runOnIdle { assertEquals(categories.created?.id, pins.saved?.categoryId) }
    }

    @Test
    fun cancelAndAndroidBackReturnToTheSameSelection() {
        compose.onNodeWithText("Voiture").performClick()
        compose.onNodeWithText("Créer une catégorie").performClick()
        compose.onNodeWithTag("category_name").performTextInput("Draft")
        compose.onNodeWithText("Annuler").performClick()
        compose.onNodeWithText("Voiture").assertIsSelected()
        compose.onNodeWithText("Créer une catégorie").performClick()
        compose.onNodeWithText("Nouvelle catégorie").assertIsDisplayed()
        // The system Back must exercise navigation rather than an asynchronously closing IME.
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.waitForIdle()
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        compose.waitUntil { viewModel.uiState.value is AddPinUiState.CategorySelection }
        compose.onNodeWithText("Voiture").assertIsSelected()
        compose.runOnIdle {
            assertEquals(location, (viewModel.uiState.value as AddPinUiState.CategorySelection).location)
            assertEquals(null, categories.created)
        }
    }

    @Test
    fun creationErrorKeepsFormAndRetryWorks() {
        compose.onNodeWithText("Créer une catégorie").performClick()
        compose.onNodeWithTag("category_name").performTextInput("Camping")
        categories.fail = true
        compose.onNodeWithTag("create_category").performClick()
        compose.onNodeWithText("La catégorie n’a pas pu être créée. Tes choix sont conservés. Réessaie.").assertIsDisplayed()
        categories.fail = false
        compose.onNodeWithTag("create_category").performClick()
        compose.onNodeWithText("Camping").assertIsSelected()
    }

    private class TestCategories : CategoryRepository {
        private val rows = MutableStateFlow(listOf(Category(SystemCategoryIds.CAR, true)))
        var created: Category? = null
        var fail = false

        override fun observeCategories(): Flow<List<Category>> = rows

        override suspend fun updateCustomCategory(
            categoryId: com.laurentvrevin.wheris.core.model.CategoryId,
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

        override suspend fun getCategoryUsageCount(categoryId: com.laurentvrevin.wheris.core.model.CategoryId): Int = error("Not used")

        override suspend fun deleteCustomCategory(
            categoryId: com.laurentvrevin.wheris.core.model.CategoryId,
        ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

        override suspend fun reassignAndDeleteCustomCategory(
            sourceCategoryId: com.laurentvrevin.wheris.core.model.CategoryId,
            replacementCategoryId: com.laurentvrevin.wheris.core.model.CategoryId,
        ): com.laurentvrevin.wheris.domain.repository.CategoryMutationResult = error("Not used")

        override fun observeSystemCategories(): Flow<List<Category>> = error("Must observe all")

        override suspend fun createCustomCategory(
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): Category {
            if (fail) error("Simulated failure")
            return Category(CategoryId("new-category"), false, name, iconKey, colorKey, 1000L).also { created = it }
        }
    }

    private class TestPins : PinRepository {
        var saved: Pin? = null

        override suspend fun savePin(pin: Pin) {
            saved = pin
        }

        override fun observePins(): Flow<List<Pin>> = MutableStateFlow(emptyList())

        override fun observePin(pinId: PinId): Flow<Pin?> = MutableStateFlow(null)

        override suspend fun deletePin(pinId: PinId) = error("Not used")

        override suspend fun updatePin(
            update: PinUpdate,
            updatedAtEpochMillis: Long,
        ): PinUpdateResult = error("Not used")
    }
}
