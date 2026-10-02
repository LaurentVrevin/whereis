package com.laurentvrevin.wheris.feature.pindetail.di

import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import com.laurentvrevin.wheris.feature.pindetail.EditPinViewModel
import com.laurentvrevin.wheris.feature.pindetail.PinDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pinDetailModule =
    module {
        viewModel {
            EditPinViewModel(
                pinRepository = get(),
                categoryRepository = get(),
                updatePin = UpdatePinUseCase(get()),
                photoStorage = get(),
                savedStateHandle = get(),
                cleanupScope = get(named("photoOperations")),
            )
        }
        viewModel {
            PinDetailViewModel(
                pinRepository = get(),
                userLocationRepository = get(),
                categoryRepository = get(),
            )
        }
    }
