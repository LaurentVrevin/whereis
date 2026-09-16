package com.laurentvrevin.androidstarter.di

import com.laurentvrevin.androidstarter.BuildConfig
import com.laurentvrevin.androidstarter.data.remote.NetworkConfig
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
