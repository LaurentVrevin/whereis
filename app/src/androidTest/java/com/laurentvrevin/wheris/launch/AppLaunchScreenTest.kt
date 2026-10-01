package com.laurentvrevin.wheris.launch

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppLaunchScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingShowsNeitherMapNorOnboarding() {
        showLaunch(AppLaunchState.Loading)
        composeRule.onNodeWithTag("launch_loading").assertIsDisplayed()
        composeRule.onNodeWithText("onboarding").assertDoesNotExist()
        composeRule.onNodeWithText("map").assertDoesNotExist()
    }

    @Test
    fun onboardingRequiredSelectsOnlyOnboarding() {
        showLaunch(AppLaunchState.OnboardingRequired)
        composeRule.onNodeWithText("onboarding").assertIsDisplayed()
        composeRule.onNodeWithText("map").assertDoesNotExist()
    }

    @Test
    fun mainAppSelectsOnlyMainNavigation() {
        showLaunch(AppLaunchState.MainApp)
        composeRule.onNodeWithText("map").assertIsDisplayed()
        composeRule.onNodeWithText("onboarding").assertDoesNotExist()
    }

    private fun showLaunch(state: AppLaunchState) {
        composeRule.setContent {
            WherisTheme {
                AppLaunchScreen(state = state, onboarding = { Text("onboarding") }, mainApp = { Text("map") })
            }
        }
    }
}
