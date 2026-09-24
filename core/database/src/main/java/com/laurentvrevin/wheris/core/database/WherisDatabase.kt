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
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    seedSystemCategories(db)
                }
            }
        }

        private fun seedSystemCategories(db: SupportSQLiteDatabase) {
            SystemCategoryIds.ALL.forEach { categoryId ->
                db.execSQL(
                    "INSERT OR IGNORE INTO categories (id, isSystem) VALUES (?, 1)",
                    arrayOf(categoryId.value),
                )
            }
        }
    }
}
