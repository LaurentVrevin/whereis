package com.laurentvrevin.wheris.feature.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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
    fun privacyContinueShowsLocationWithoutRequestingPermission() {
        var continuationCalls = 0
        showOnboarding(onRequestPermission = { continuationCalls++ })
        continueOnboarding()
        continueOnboarding()
        composeRule.runOnIdle { assertEquals(0, continuationCalls) }

        continueOnboarding()

        composeRule.runOnIdle { assertEquals(0, continuationCalls) }
        assertTextIsDisplayed(R.string.onboarding_location_title)
        assertTextIsDisplayed(R.string.onboarding_allow_location)
    }

    @Test
    fun backFromLocationShowsPrivacy() {
        showOnboarding()
        repeat(3) { continueOnboarding() }
        composeRule.onNodeWithText(text(R.string.onboarding_back)).performClick()
        assertTextIsDisplayed(R.string.onboarding_privacy_title)
    }

    @Test
    fun systemBackNavigatesThroughThePreviousPages() {
        showOnboarding()
        continueOnboarding()
        continueOnboarding()
        continueOnboarding()

        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
        assertTextIsDisplayed(R.string.onboarding_privacy_title)
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
                OnboardingContent(
                    permissionState = LocationPermissionState.NotRequested,
                    completionState = OnboardingCompletionState.Idle,
                    onRequestPermission = {},
                    onOpenSettings = {},
                    onComplete = {},
                )
            }
        }
        continueOnboarding()
        continueOnboarding()
        continueOnboarding()

        restorationTester.emulateSavedInstanceStateRestore()

        assertTextIsDisplayed(R.string.onboarding_location_title)
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

    @Test
    fun deniedStateOnlyRetriesOnClickAndAllowsCompletionWithoutPermission() {
        var requests = 0
        var completions = 0
        showOnboarding(
            permissionState = LocationPermissionState.Denied,
            onRequestPermission = { requests++ },
            onComplete = { completions++ },
        )
        repeat(3) { continueOnboarding() }
        composeRule.runOnIdle {
            assertEquals(0, requests)
            assertEquals(0, completions)
        }
        composeRule.onNodeWithText(text(R.string.onboarding_permission_retry)).performClick()
        composeRule.runOnIdle { assertEquals(1, requests) }
        composeRule.onNodeWithText(text(R.string.onboarding_continue_without_location)).performClick()
        composeRule.runOnIdle { assertEquals(1, completions) }
    }

    @Test
    fun settingsRequiredExposesSettingsActionInsteadOfPermissionRetry() {
        var settings = 0
        showOnboarding(permissionState = LocationPermissionState.SettingsRequired, onOpenSettings = { settings++ })
        repeat(3) { continueOnboarding() }
        composeRule.onNodeWithText(text(R.string.onboarding_permission_retry)).assertDoesNotExist()
        composeRule.onNodeWithText(text(R.string.onboarding_open_settings)).performClick()
        composeRule.runOnIdle { assertEquals(1, settings) }
        assertTextIsDisplayed(R.string.onboarding_continue_without_location)
    }

    @Test
    fun grantedPermissionOffersCompletionWithoutAnotherRequest() {
        var requests = 0
        var completions = 0
        showOnboarding(
            permissionState = LocationPermissionState.Approximate,
            onRequestPermission = { requests++ },
            onComplete = { completions++ },
        )
        repeat(3) { continueOnboarding() }
        composeRule.onNodeWithText(text(R.string.onboarding_finish)).performClick()
        composeRule.runOnIdle {
            assertEquals(0, requests)
            assertEquals(1, completions)
        }
    }

    @Test
    fun changedPermissionAfterSettingsUpdatesTheActionsWithoutRequesting() {
        var permission by mutableStateOf(LocationPermissionState.SettingsRequired)
        var requests = 0
        composeRule.setContent {
            WherisTheme {
                OnboardingScreen(
                    page = OnboardingPage.LOCATION,
                    permissionState = permission,
                    onContinue = {},
                    onBack = {},
                    onRequestPermission = { requests++ },
                )
            }
        }
        assertTextIsDisplayed(R.string.onboarding_open_settings)
        composeRule.runOnIdle { permission = LocationPermissionState.Precise }
        assertTextIsDisplayed(R.string.onboarding_finish)
        composeRule.runOnIdle { assertEquals(0, requests) }
    }

    @Test
    fun savingDisablesActionsAndFailureAllowsRetry() {
        var completion by mutableStateOf(OnboardingCompletionState.Saving)
        var retries = 0
        composeRule.setContent {
            WherisTheme {
                OnboardingScreen(
                    page = OnboardingPage.LOCATION,
                    permissionState = LocationPermissionState.Denied,
                    completionState = completion,
                    onContinue = {},
                    onBack = {},
                    onComplete = { retries++ },
                )
            }
        }
        composeRule.onNodeWithText(text(R.string.onboarding_saving)).assertIsNotEnabled()
        composeRule.onNodeWithText(text(R.string.onboarding_continue_without_location)).assertIsNotEnabled()
        composeRule.onNodeWithText(text(R.string.onboarding_back)).assertIsNotEnabled()
        composeRule.runOnIdle { completion = OnboardingCompletionState.Failed }
        assertTextIsDisplayed(R.string.onboarding_completion_error)
        composeRule.onNodeWithText(text(R.string.onboarding_completion_retry)).performClick()
        composeRule.runOnIdle { assertEquals(1, retries) }
    }

    @Test
    fun locationRecoveryActionsAreReachableWithLargeText() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                WherisTheme(darkTheme = true) {
                    OnboardingScreen(
                        page = OnboardingPage.LOCATION,
                        permissionState = LocationPermissionState.SettingsRequired,
                        onContinue = {},
                        onBack = {},
                    )
                }
            }
        }
        composeRule.onNodeWithText(text(R.string.onboarding_location_denied)).performScrollTo().assertIsDisplayed()
        assertTextIsDisplayed(R.string.onboarding_open_settings)
        assertTextIsDisplayed(R.string.onboarding_continue_without_location)
        assertTextIsDisplayed(R.string.onboarding_back)
    }

    private fun showOnboarding(
        permissionState: LocationPermissionState = LocationPermissionState.NotRequested,
        onRequestPermission: () -> Unit = {},
        onOpenSettings: () -> Unit = {},
        onComplete: () -> Unit = {},
    ) {
        composeRule.setContent {
            WherisTheme {
                OnboardingContent(
                    permissionState = permissionState,
                    completionState = OnboardingCompletionState.Idle,
                    onRequestPermission = onRequestPermission,
                    onOpenSettings = onOpenSettings,
                    onComplete = onComplete,
                )
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
