package com.laurentvrevin.wheris.data.di

import androidx.room.Room
import com.laurentvrevin.wheris.data.local.AppDatabase
import com.laurentvrevin.wheris.data.local.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule =
    module {

        // Database
        single {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "android_starter_db",
            ).build()
        }

        // DataStore
        single { AppPreferences(androidContext()) }

        // DAOs
        single { get<AppDatabase>().templateDao() }
    }
