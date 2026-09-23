package com.laurentvrevin.wheris.feature.addpin.di

import com.laurentvrevin.wheris.feature.addpin.AddPinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addPinModule =
    module {
        viewModel {
            AddPinViewModel(
                userLocationRepository = get(),
            )
        }
    }
