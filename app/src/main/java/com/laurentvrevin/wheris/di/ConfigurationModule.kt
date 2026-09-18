package com.laurentvrevin.wheris.di

import com.laurentvrevin.wheris.BuildConfig
import com.laurentvrevin.wheris.data.remote.NetworkConfig
import org.koin.dsl.module

val configurationModule =
    module {
        single {
            NetworkConfig(
                baseUrl = BuildConfig.API_BASE_URL,
                isDebug = BuildConfig.DEBUG,
            )
        }
    }
