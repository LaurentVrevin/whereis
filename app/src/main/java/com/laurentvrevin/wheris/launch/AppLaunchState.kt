package com.laurentvrevin.wheris.launch

sealed interface AppLaunchState {
    data object Loading : AppLaunchState

    data object OnboardingRequired : AppLaunchState

    data object MainApp : AppLaunchState
}
