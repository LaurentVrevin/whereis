package com.laurentvrevin.wheris.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE pins ADD COLUMN name TEXT")
            db.execSQL("ALTER TABLE pins ADD COLUMN note TEXT")
        }
    }
