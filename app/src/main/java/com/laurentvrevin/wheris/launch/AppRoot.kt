package com.laurentvrevin.wheris.launch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.feature.onboarding.OnboardingRoute
import com.laurentvrevin.wheris.navigation.AppNavHost
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRoot(
    modifier: Modifier = Modifier,
    viewModel: AppLaunchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppLaunchScreen(
        state = state,
        onboarding = { OnboardingRoute() },
        mainApp = { AppNavHost() },
        modifier = modifier,
    )
}

@Composable
internal fun AppLaunchScreen(
    state: AppLaunchState,
    onboarding: @Composable () -> Unit,
    mainApp: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when (state) {
            AppLaunchState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().testTag("launch_loading"), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            AppLaunchState.OnboardingRequired -> onboarding()
            AppLaunchState.MainApp -> mainApp()
        }
    }
}
