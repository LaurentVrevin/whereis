package com.laurentvrevin.androidstarter.data.di

import androidx.room.Room
import com.laurentvrevin.androidstarter.data.local.AppDatabase
import com.laurentvrevin.androidstarter.data.local.AppPreferences
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
