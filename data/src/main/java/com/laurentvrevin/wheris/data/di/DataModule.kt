package com.laurentvrevin.wheris.data.di

import com.laurentvrevin.wheris.data.repository.PinRepositoryImpl
import com.laurentvrevin.wheris.domain.PinRepository
import org.koin.dsl.module

val dataModule =
    module {
        single<PinRepository> { PinRepositoryImpl(get()) }
    }
