package com.laurentvrevin.wheris.feature.onboarding.di

import com.laurentvrevin.wheris.feature.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule =
    module {
        viewModel { OnboardingViewModel(get()) }
    }
