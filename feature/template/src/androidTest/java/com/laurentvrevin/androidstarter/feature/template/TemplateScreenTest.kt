package com.laurentvrevin.androidstarter.feature.template

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateItem
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateRepository
import com.laurentvrevin.androidstarter.feature.template.presentation.TemplateScreen
import com.laurentvrevin.androidstarter.feature.template.presentation.TemplateViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class TemplateScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun templateScreen_showsEmptyState_whenNoItems() {
        val fakeRepo =
            object : TemplateRepository {
                override fun getTemplates(): Flow<List<TemplateItem>> = MutableStateFlow(emptyList())

                override suspend fun addTemplate(
                    title: String,
                    description: String,
                ) {}

                override suspend fun deleteTemplate(id: Int) {}
            }
        val viewModel = TemplateViewModel(fakeRepo)

        composeTestRule.setContent {
            TemplateScreen(onBack = {}, viewModel = viewModel)
        }

        // Check if empty state message is displayed
        // "No data available in local Room database."
        composeTestRule.onNodeWithText("No data available in local Room database.").assertIsDisplayed()
    }
}
