package com.laurentvrevin.wheris.feature.pins.di

import com.laurentvrevin.wheris.feature.pins.PinsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pinsModule =
    module {
        viewModel { PinsViewModel(get()) }
    }
