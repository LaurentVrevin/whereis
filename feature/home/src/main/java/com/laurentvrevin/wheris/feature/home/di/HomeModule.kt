package com.laurentvrevin.wheris.feature.home.di

import com.laurentvrevin.wheris.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule =
    module {
        viewModel {
            HomeViewModel(
                pinRepository = get(),
                userLocationRepository = get(),
            )
        }
    }
