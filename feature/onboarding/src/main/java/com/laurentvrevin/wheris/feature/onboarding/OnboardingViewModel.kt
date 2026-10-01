package com.laurentvrevin.wheris.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.domain.repository.OnboardingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class OnboardingCompletionState {
    Idle,
    Saving,
    Failed,
    Completed,
}

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {
    private val _completionState = MutableStateFlow(OnboardingCompletionState.Idle)
    val completionState = _completionState.asStateFlow()

    fun complete() {
        if (_completionState.value == OnboardingCompletionState.Saving ||
            _completionState.value == OnboardingCompletionState.Completed
        ) {
            return
        }

        _completionState.value = OnboardingCompletionState.Saving
        viewModelScope.launch {
            try {
                onboardingRepository.setOnboardingCompleted(true)
                _completionState.value = OnboardingCompletionState.Completed
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _completionState.value = OnboardingCompletionState.Failed
            }
        }
    }
}
