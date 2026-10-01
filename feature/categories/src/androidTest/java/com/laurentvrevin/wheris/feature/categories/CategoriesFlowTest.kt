package com.laurentvrevin.wheris.feature.categories

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesFlowTest {
    @get:Rule
    val compose = createComposeRule()
    private val repository = TestCategories()
    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setUp() {
        compose.runOnUiThread { viewModel = CategoriesViewModel(repository) }
        compose.waitUntil { !viewModel.uiState.value.isLoading }
    }

    private fun content(
        dark: Boolean = false,
        fontScale: Float = 1f,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                WherisTheme(darkTheme = dark) { CategoriesRoute(onBack = {}, viewModel = viewModel) }
            }
        }
    }

    private fun openCustom() {
        compose.onNodeWithTag("categories_list").performScrollToNode(hasTestTag("edit_source"))
        compose.onNodeWithTag("edit_source").performClick()
    }

    @Test
    fun defaultsAreReadOnlyAndCustomEditingSupportsDarkLargeFontAndLongNames() {
        compose.runOnUiThread {
            repository.rows.value +=
                List(200) { index ->
                    repository.custom.copy(id = CategoryId("extra-$index"), name = "散歩 — Longue catégorie personnalisée $index")
                }
        }
        content(dark = true, fontScale = 1.5f)
        compose.onNodeWithTag("system_car").assertHasNoClickAction()
        compose.onNode(hasText("Par défaut") and hasAnyAncestor(hasTestTag("system_car"))).assertIsDisplayed()
        compose.onNodeWithTag("delete_category").assertDoesNotExist()
        compose.onNodeWithTag("categories_list").performScrollToNode(hasTestTag("edit_extra-199"))
        compose.onNodeWithTag("edit_extra-199").assertIsDisplayed()
        compose.onNodeWithTag("categories_list").performScrollToIndex(0)
        openCustom()
        compose.onNodeWithText("Modifier une catégorie").assertIsDisplayed()
        compose.onNodeWithTag("category_name").assertTextContains(repository.custom.name!!)
        compose.onNodeWithTag("delete_category").assertIsDisplayed()
        compose.onNodeWithText("Annuler").performClick()
        compose.onNodeWithTag("categories_list").assertIsDisplayed()
    }

    @Test
    fun createAndEditUseTheSameLivePreviewAndReturnToList() {
        content()
        compose.onNodeWithText("Créer une catégorie").performClick()
        compose.onNodeWithTag("save_category").assertIsNotEnabled()
        compose.onNodeWithTag("category_name").performTextInput("  散歩 — Promenades  ")
        compose.onNodeWithTag("icon_park").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("color_PURPLE").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("category_editor").performScrollToIndex(3)
        compose.onNode(hasText("散歩 — Promenades") and hasAnyAncestor(hasTestTag("category_preview"))).assertIsDisplayed()
        compose.onNodeWithTag("save_category").performClick()
        compose.onNodeWithTag("categories_list").performScrollToNode(hasTestTag("edit_new"))
        compose.onNodeWithTag("edit_new").assertTextContains("散歩 — Promenades").performClick()
        compose.onNodeWithTag("category_name").performTextReplacement("更新 — Nouveau nom")
        compose.onNodeWithTag("save_category").performClick()
        compose.onNodeWithTag("categories_list").performScrollToNode(hasTestTag("edit_new"))
        compose.onNodeWithTag("edit_new").assertTextContains("更新 — Nouveau nom")
        compose.runOnIdle {
            val created = repository.rows.value.last()
            assertEquals(CategoryId("new"), created.id)
            assertEquals(2000L, created.createdAtEpochMillis)
        }
    }

    @Test
    fun unusedDeletionIdentifiesCategoryAndRequiresConfirmation() {
        content()
        openCustom()
        compose.onNodeWithTag("delete_category").performClick()
        compose.onNodeWithTag("delete_unused_dialog").assertIsDisplayed()
        compose.onNode(
            hasText("Supprimer « ${repository.custom.name} » ? Aucun lieu n’utilise cette catégorie.") and
                hasAnyAncestor(hasTestTag("delete_unused_dialog")),
        ).assertIsDisplayed()
        compose.onNode(hasText("Annuler") and hasAnyAncestor(hasTestTag("delete_unused_dialog"))).performClick()
        compose.onNodeWithTag("delete_category").performClick()
        compose.onNode(hasText("Supprimer") and hasAnyAncestor(hasTestTag("delete_unused_dialog"))).performClick()
        compose.onNodeWithTag("categories_list").assertIsDisplayed()
        compose.onNodeWithTag("edit_source").assertDoesNotExist()
    }

    @Test
    fun usedDeletionRequiresExplicitAccessibleChoiceAndKeepsReadableErrorForRetry() {
        repository.count = 2
        content()
        openCustom()
        compose.onNodeWithTag("delete_category").performClick()
        compose.onNodeWithText("2 lieux concernés").assertIsDisplayed()
        compose.onNodeWithTag("replacement_source").assertDoesNotExist()
        compose.onNodeWithTag("confirm_reassign").assertIsNotEnabled()
        compose.onNodeWithTag("replacement_other").performScrollTo().assertTextContains("Autre").performClick().assertIsSelected()
        repository.result = CategoryMutationResult.TECHNICAL_FAILURE
        compose.onNodeWithTag("confirm_reassign").performClick()
        compose.onNodeWithText("La suppression n’a pas abouti. Tes lieux sont conservés. Réessaie.").assertIsDisplayed()
        repository.result = CategoryMutationResult.SUCCESS
        compose.onNodeWithTag("confirm_reassign").performClick()
        compose.onNodeWithTag("categories_list").assertIsDisplayed()
        compose.runOnIdle { assertEquals(SystemCategoryIds.OTHER, repository.replacement) }
    }

    @Test
    fun singularUsageAndPendingMutationDisableActionsAndAndroidBack() {
        repository.count = 1
        content()
        openCustom()
        compose.onNodeWithTag("delete_category").performClick()
        compose.onNodeWithText("1 lieu concerné").assertIsDisplayed()
        compose.onNodeWithTag("replacement_other").performScrollTo().performClick()
        repository.pause = CompletableDeferred()
        compose.onNodeWithTag("confirm_reassign").performClick().assertIsNotEnabled()
        compose.onNodeWithText("En cours…").assertIsDisplayed()
        compose.onNodeWithText("Annuler").assertIsNotEnabled()
        compose.waitForIdle()
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Supprimer la catégorie").assertIsDisplayed()
        compose.runOnIdle { repository.pause?.complete(Unit) }
        compose.onNodeWithTag("categories_list").assertIsDisplayed()
    }

    private class TestCategories : CategoryRepository {
        val custom =
            Category(
                CategoryId("source"),
                false,
                "散歩 — Mes longues balades au bord de la rivière",
                CategoryIconKey.PARK,
                CategoryColorKey.PURPLE,
                1000L,
            )
        val rows = MutableStateFlow(listOf(Category(SystemCategoryIds.CAR, true), Category(SystemCategoryIds.OTHER, true), custom))
        var count = 0
        var result = CategoryMutationResult.SUCCESS
        var replacement: CategoryId? = null
        var pause: CompletableDeferred<Unit>? = null

        override fun observeCategories(): Flow<List<Category>> = rows

        override fun observeSystemCategories(): Flow<List<Category>> = error("Must observe all")

        override suspend fun createCustomCategory(
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): Category = Category(CategoryId("new"), false, name, iconKey, colorKey, 2000L).also { rows.value += it }

        override suspend fun updateCustomCategory(
            categoryId: CategoryId,
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): CategoryMutationResult {
            rows.value = rows.value.map { if (it.id == categoryId) it.copy(name = name, iconKey = iconKey, colorKey = colorKey) else it }
            return CategoryMutationResult.SUCCESS
        }

        override suspend fun getCategoryUsageCount(categoryId: CategoryId): Int = count

        override suspend fun deleteCustomCategory(categoryId: CategoryId): CategoryMutationResult {
            rows.value = rows.value.filterNot { it.id == categoryId }
            return CategoryMutationResult.SUCCESS
        }

        override suspend fun reassignAndDeleteCustomCategory(
            sourceCategoryId: CategoryId,
            replacementCategoryId: CategoryId,
        ): CategoryMutationResult {
            pause?.await()
            if (result == CategoryMutationResult.SUCCESS) {
                replacement = replacementCategoryId
                rows.value = rows.value.filterNot { it.id == sourceCategoryId }
            }
            return result
        }
    }
}
