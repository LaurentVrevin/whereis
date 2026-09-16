package com.laurentvrevin.androidstarter.feature.template.di

import com.laurentvrevin.androidstarter.feature.template.data.TemplateRepositoryImpl
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateRepository
import com.laurentvrevin.androidstarter.feature.template.presentation.TemplateViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val templateModule =
    module {
        single<TemplateRepository> { TemplateRepositoryImpl(get()) }
        viewModel { TemplateViewModel(get()) }
    }
