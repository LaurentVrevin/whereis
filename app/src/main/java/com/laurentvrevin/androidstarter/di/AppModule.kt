package com.laurentvrevin.androidstarter.di

import com.laurentvrevin.androidstarter.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        viewModel { MainViewModel(get()) }
    }
