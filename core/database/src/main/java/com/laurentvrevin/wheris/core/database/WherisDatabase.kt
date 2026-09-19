package com.laurentvrevin.wheris.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.laurentvrevin.wheris.core.database.dao.CategoryDao
import com.laurentvrevin.wheris.core.database.dao.PinDao
import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.SystemCategoryIds

@Database(
    entities = [
        PinEntity::class,
        CategoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class WherisDatabase : RoomDatabase() {
    abstract fun pinDao(): PinDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        fun getCallback(): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed OTHER category with stable internal ID
                    db.execSQL(
                        "INSERT OR IGNORE INTO categories (id, isSystem) VALUES ('${SystemCategoryIds.OTHER.value}', 1)",
                    )
                }
            }
        }
    }
}
