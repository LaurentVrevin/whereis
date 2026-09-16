package com.laurentvrevin.androidstarter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laurentvrevin.androidstarter.data.local.database.template.TemplateDao
import com.laurentvrevin.androidstarter.data.local.database.template.TemplateEntity

@Database(
    entities = [TemplateEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
}
