package com.laurentvrevin.wheris.feature.addpin.di

import com.laurentvrevin.wheris.domain.usecase.GetCurrentLocationUseCase
import com.laurentvrevin.wheris.feature.addpin.AddPinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addPinModule =
    module {
        factory { GetCurrentLocationUseCase(userLocationRepository = get()) }
        viewModel {
            AddPinViewModel(
                getCurrentLocationUseCase = get(),
            )
        }
    }
