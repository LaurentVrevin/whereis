package com.laurentvrevin.wheris.core.datastore.di

import com.laurentvrevin.wheris.core.datastore.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val datastoreModule =
    module {
        single { AppPreferences(androidContext()) }
    }
