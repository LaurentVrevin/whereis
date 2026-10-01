package com.laurentvrevin.wheris.feature.addpin

import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.core.ui.category.categoryColor
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CategoryAccessibilityTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun darkEditorSupportsLargeFontLongInternationalNameAndApprovedAccent() {
        val name = "散歩 — Mes longues balades au bord de la rivière"
        var actualAccent = Color.Unspecified
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                WherisTheme(darkTheme = true) {
                    val accent = categoryColor(CategoryColorKey.PURPLE)
                    SideEffect { actualAccent = accent }
                    CreateCategoryScreen(
                        AddPinUiState.CategoryCreation(
                            selection = AddPinUiState.CategorySelection(UserLocation(GeoPoint(12.0, 24.0), 8f, 35.0, 1000L)),
                            name = name,
                            iconKey = CategoryIconKey.PARK,
                            colorKey = CategoryColorKey.PURPLE,
                        ),
                        onNameChange = {},
                        onIconChange = {},
                        onColorChange = {},
                        onCreate = {},
                        onCancel = {},
                    )
                }
            }
        }
        compose.onNodeWithTag("icon_park").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("color_PURPLE").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("category_editor").performScrollToIndex(3)
        compose.onNode(hasText(name) and hasAnyAncestor(hasTestTag("category_preview"))).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("create_category").assertIsDisplayed().assertIsEnabled()
        compose.runOnIdle { assertEquals(Color(0xFFC084FC), actualAccent) }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.getExternalFilesDir(null), "category-dark-large-font.png")
        compose.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun lightThemeUsesApprovedAccentAndEveryNamedColorCanBeDisplayed() {
        var actualAccent = Color.Unspecified
        compose.setContent {
            WherisTheme(darkTheme = false) {
                val accent = categoryColor(CategoryColorKey.PURPLE)
                SideEffect { actualAccent = accent }
                CreateCategoryScreen(
                    AddPinUiState.CategoryCreation(
                        selection = AddPinUiState.CategorySelection(UserLocation(GeoPoint(12.0, 24.0), 8f, 35.0, 1000L)),
                    ),
                    onNameChange = {},
                    onIconChange = {},
                    onColorChange = {},
                    onCreate = {},
                    onCancel = {},
                )
            }
        }
        CategoryColorKey.entries.forEach { compose.onNodeWithTag("color_${it.name}").performScrollTo().assertIsDisplayed() }
        compose.runOnIdle { assertEquals(Color(0xFF7E22CE), actualAccent) }
    }
}
