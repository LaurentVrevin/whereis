package com.laurentvrevin.wheris.feature.addpin.di

import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import com.laurentvrevin.wheris.feature.addpin.AddPinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addPinModule =
    module {
        factory {
            CreatePinUseCase(pinRepository = get())
        }
        viewModel {
            AddPinViewModel(
                userLocationRepository = get(),
                categoryRepository = get(),
                createPinUseCase = get(),
            )
        }
    }
