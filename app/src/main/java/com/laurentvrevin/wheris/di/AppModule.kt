package com.laurentvrevin.wheris.di

import com.laurentvrevin.wheris.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        viewModel { MainViewModel(get()) }
    }
