package com.laurentvrevin.wheris.di

import com.laurentvrevin.wheris.launch.AppLaunchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appLaunchModule =
    module {
        viewModel { AppLaunchViewModel(get()) }
    }
