package com.laurentvrevin.wheris.feature.template.di

import com.laurentvrevin.wheris.feature.template.data.TemplateRepositoryImpl
import com.laurentvrevin.wheris.feature.template.domain.TemplateRepository
import com.laurentvrevin.wheris.feature.template.presentation.TemplateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val templateModule =
    module {
        single<TemplateRepository> { TemplateRepositoryImpl(get()) }
        viewModel { TemplateViewModel(get()) }
    }
