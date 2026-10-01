package com.laurentvrevin.wheris.core.database.di

import androidx.room.Room
import com.laurentvrevin.wheris.core.database.MIGRATION_1_2
import com.laurentvrevin.wheris.core.database.MIGRATION_2_3
import com.laurentvrevin.wheris.core.database.WherisDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            Room.databaseBuilder(
                androidContext(),
                WherisDatabase::class.java,
                "wheris.db",
            ).addCallback(WherisDatabase.getCallback())
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
        }

        single { get<WherisDatabase>().pinDao() }
        single { get<WherisDatabase>().categoryDao() }
    }
