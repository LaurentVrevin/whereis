package com.laurentvrevin.androidstarter.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.androidstarter.data.local.database.template.TemplateDao
import com.laurentvrevin.androidstarter.data.local.database.template.TemplateEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var templateDao: TemplateDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        templateDao = db.templateDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeTemplateAndReadInList() =
        runBlocking {
            val template = TemplateEntity(title = "Title", description = "Desc")
            templateDao.insert(template)
            val items = templateDao.getAllTemplates().first()
            assertEquals(items[0].title, "Title")
        }

    @Test
    @Throws(Exception::class)
    fun writeTemplateAndGetById() =
        runBlocking {
            val template = TemplateEntity(id = 10, title = "Test", description = "Desc")
            templateDao.upsert(template)
            val loaded = templateDao.getAllTemplates().first().find { it.id == 10 }
            assertEquals(loaded?.title, "Test")
        }
}
