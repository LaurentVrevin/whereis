package com.laurentvrevin.wheris.feature.pindetail.di

import com.laurentvrevin.wheris.domain.usecase.UpdatePinDetailsUseCase
import com.laurentvrevin.wheris.feature.pindetail.EditPinViewModel
import com.laurentvrevin.wheris.feature.pindetail.PinDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pinDetailModule =
    module {
        viewModel {
            EditPinViewModel(
                pinRepository = get(),
                updateDetails = UpdatePinDetailsUseCase(get()),
                savedStateHandle = get(),
            )
        }
        viewModel {
            PinDetailViewModel(
                pinRepository = get(),
                userLocationRepository = get(),
            )
        }
    }
