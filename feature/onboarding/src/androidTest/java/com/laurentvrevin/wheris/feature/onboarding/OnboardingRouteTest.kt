package com.laurentvrevin.wheris.feature.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingRouteTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun initialPageIsWelcome() {
        showOnboarding()

        assertTextIsDisplayed(R.string.onboarding_brand)
        assertTextIsDisplayed(R.string.onboarding_welcome_title)
        assertTextIsDisplayed(R.string.onboarding_continue)
        composeRule.onNodeWithText(text(R.string.onboarding_back)).assertDoesNotExist()
    }

    @Test
    fun continueFromWelcomeShowsConcept() {
        showOnboarding()

        continueOnboarding()

        assertTextIsDisplayed(R.string.onboarding_concept_title)
        composeRule.onNodeWithText(text(R.string.onboarding_concept_find)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun continueFromConceptShowsPrivacy() {
        showOnboarding()
        continueOnboarding()

        continueOnboarding()

        assertTextIsDisplayed(R.string.onboarding_privacy_title)
    }

    @Test
    fun backFromConceptShowsWelcome() {
        showOnboarding()
        continueOnboarding()

        composeRule.onNodeWithText(text(R.string.onboarding_back)).performClick()

        assertTextIsDisplayed(R.string.onboarding_welcome_title)
    }

    @Test
    fun backFromPrivacyShowsConcept() {
        showOnboarding()
        continueOnboarding()
        continueOnboarding()

        composeRule.onNodeWithText(text(R.string.onboarding_back)).performClick()

        assertTextIsDisplayed(R.string.onboarding_concept_title)
    }

    @Test
    fun finalContinueOnlyCallsTheCallbackAndKeepsPrivacyPage() {
        var continuationCalls = 0
        showOnboarding(onContinue = { continuationCalls++ })
        continueOnboarding()
        continueOnboarding()
        composeRule.runOnIdle { assertEquals(0, continuationCalls) }

        continueOnboarding()

        composeRule.runOnIdle { assertEquals(1, continuationCalls) }
        assertTextIsDisplayed(R.string.onboarding_privacy_title)
    }

    @Test
    fun systemBackNavigatesThroughThePreviousPages() {
        showOnboarding()
        continueOnboarding()
        continueOnboarding()

        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        assertTextIsDisplayed(R.string.onboarding_concept_title)
        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        assertTextIsDisplayed(R.string.onboarding_welcome_title)
    }

    @Test
    fun currentPageSurvivesSavedStateRestoration() {
        val restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            WherisTheme {
                OnboardingRoute(onContinue = {})
            }
        }
        continueOnboarding()
        continueOnboarding()

        restorationTester.emulateSavedInstanceStateRestore()

        assertTextIsDisplayed(R.string.onboarding_privacy_title)
    }

    @Test
    fun privacyFactsAndActionsAreReachableWithLargeTextInDarkTheme() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                WherisTheme(darkTheme = true) {
                    OnboardingScreen(page = OnboardingPage.PRIVACY, onContinue = {}, onBack = {})
                }
            }
        }

        listOf(
            R.string.onboarding_privacy_local,
            R.string.onboarding_privacy_account,
            R.string.onboarding_privacy_tracking,
            R.string.onboarding_privacy_server,
        ).forEach { fact ->
            composeRule.onNodeWithText(text(fact)).performScrollTo().assertIsDisplayed()
        }
        assertTextIsDisplayed(R.string.onboarding_continue)
        assertTextIsDisplayed(R.string.onboarding_back)
    }

    private fun showOnboarding(onContinue: () -> Unit = {}) {
        composeRule.setContent {
            WherisTheme {
                OnboardingRoute(onContinue = onContinue)
            }
        }
    }

    private fun continueOnboarding() {
        composeRule.onNodeWithText(text(R.string.onboarding_continue)).performClick()
    }

    private fun assertTextIsDisplayed(resourceId: Int) {
        composeRule.onNodeWithText(text(resourceId)).assertIsDisplayed()
    }

    private fun text(resourceId: Int): String = composeRule.activity.getString(resourceId)
}
