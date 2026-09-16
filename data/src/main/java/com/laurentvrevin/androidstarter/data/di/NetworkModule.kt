package com.laurentvrevin.androidstarter.data.di

import com.laurentvrevin.androidstarter.data.remote.KtorClientFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

val networkModule =
    module {
        single { KtorClientFactory.create(OkHttp.create(), get()) }
    }
