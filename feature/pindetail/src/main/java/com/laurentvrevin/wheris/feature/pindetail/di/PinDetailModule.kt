package com.laurentvrevin.wheris.feature.pindetail.di

import com.laurentvrevin.wheris.feature.pindetail.PinDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pinDetailModule =
    module {
        viewModel {
            PinDetailViewModel(
                pinRepository = get(),
                userLocationRepository = get(),
            )
        }
    }
