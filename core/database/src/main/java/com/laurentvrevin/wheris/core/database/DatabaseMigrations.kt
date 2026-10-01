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

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE categories ADD COLUMN name TEXT")
            db.execSQL("ALTER TABLE categories ADD COLUMN iconKey TEXT")
            db.execSQL("ALTER TABLE categories ADD COLUMN colorKey TEXT")
            db.execSQL("ALTER TABLE categories ADD COLUMN createdAtEpochMillis INTEGER")
            // Historical custom rows had no metadata. Zero denotes an unknown creation date.
            // IDs are never normalized: only the synthetic display name is trimmed.
            db.execSQL(
                "UPDATE categories SET name = id, iconKey = 'place', " +
                    "colorKey = 'category/orange', createdAtEpochMillis = 0 WHERE isSystem = 0",
            )
            db.query("SELECT id FROM categories WHERE isSystem = 0").use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    db.execSQL("UPDATE categories SET name = ? WHERE id = ?", arrayOf(id.trim(), id))
                }
            }
        }
    }
