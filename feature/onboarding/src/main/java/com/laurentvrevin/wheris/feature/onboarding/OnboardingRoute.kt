package com.laurentvrevin.wheris.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun OnboardingRoute(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var page by rememberSaveable { mutableStateOf(OnboardingPage.WELCOME) }

    fun goBack() {
        page =
            when (page) {
                OnboardingPage.WELCOME -> OnboardingPage.WELCOME
                OnboardingPage.CONCEPT -> OnboardingPage.WELCOME
                OnboardingPage.PRIVACY -> OnboardingPage.CONCEPT
            }
    }

    BackHandler(enabled = page != OnboardingPage.WELCOME) { goBack() }

    OnboardingScreen(
        page = page,
        onContinue = {
            when (page) {
                OnboardingPage.WELCOME -> page = OnboardingPage.CONCEPT
                OnboardingPage.CONCEPT -> page = OnboardingPage.PRIVACY
                OnboardingPage.PRIVACY -> onContinue()
            }
        },
        onBack = { goBack() },
        modifier = modifier,
    )
}
