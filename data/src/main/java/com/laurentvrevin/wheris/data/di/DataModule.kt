package com.laurentvrevin.wheris.data.di

import com.laurentvrevin.wheris.data.repository.CategoryRepositoryImpl
import com.laurentvrevin.wheris.data.repository.PinRepositoryImpl
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import org.koin.dsl.module

val dataModule =
    module {
        single<PinRepository> { PinRepositoryImpl(get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    }
