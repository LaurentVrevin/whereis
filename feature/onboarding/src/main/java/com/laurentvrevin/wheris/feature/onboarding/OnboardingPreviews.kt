package com.laurentvrevin.wheris.feature.onboarding

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laurentvrevin.wheris.core.designsystem.theme.WherisTheme

@Preview(name = "ONB_001 — Clair", showBackground = true)
@Preview(name = "ONB_001 — Sombre", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WelcomePreview() {
    WherisTheme {
        OnboardingScreen(page = OnboardingPage.WELCOME, onContinue = {}, onBack = {})
    }
}

@Preview(name = "ONB_002 — Clair", showBackground = true)
@Preview(name = "ONB_002 — Sombre", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ConceptPreview() {
    WherisTheme {
        OnboardingScreen(page = OnboardingPage.CONCEPT, onContinue = {}, onBack = {})
    }
}

@Preview(name = "ONB_003 — Clair", showBackground = true)
@Preview(name = "ONB_003 — Sombre", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "ONB_003 — Grand texte", showBackground = true, fontScale = 2f)
@Composable
private fun PrivacyPreview() {
    WherisTheme {
        OnboardingScreen(page = OnboardingPage.PRIVACY, onContinue = {}, onBack = {})
    }
}
