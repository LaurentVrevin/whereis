package com.laurentvrevin.wheris.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laurentvrevin.wheris.core.database.dao.TemplateDao
import com.laurentvrevin.wheris.core.database.entity.TemplateEntity

@Database(
    entities = [TemplateEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class WherisDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
}
