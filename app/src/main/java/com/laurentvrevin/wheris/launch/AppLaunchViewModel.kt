package com.laurentvrevin.wheris.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppLaunchViewModel(
    onboardingRepository: OnboardingRepository,
) : ViewModel() {
    val uiState: StateFlow<AppLaunchState> =
        onboardingRepository.isOnboardingCompleted
            .map { completed ->
                if (completed) AppLaunchState.MainApp else AppLaunchState.OnboardingRequired
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppLaunchState.Loading,
            )
}
