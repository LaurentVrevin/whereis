package com.laurentvrevin.wheris.feature.categories.di

import com.laurentvrevin.wheris.feature.categories.CategoriesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val categoriesModule =
    module {
        viewModel { CategoriesViewModel(get()) }
    }
