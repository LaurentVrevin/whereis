package com.laurentvrevin.wheris.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryRepositoryPersistenceTest {
    @Test
    fun createdCategorySurvivesReopenAndIsObservableAlongsideSystemCategories() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val databaseName = "category-repository-test.db"
            context.deleteDatabase(databaseName)

            fun openDatabase(): WherisDatabase =
                Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                    .addCallback(WherisDatabase.getCallback())
                    .build()

            val firstDatabase = openDatabase()
            try {
                val category =
                    CategoryRepositoryImpl(firstDatabase.categoryDao())
                        .createCustomCategory("  Camping  ", CategoryIconKey.PARK, CategoryColorKey.GREEN)
                firstDatabase.close()
                val reopened = openDatabase()
                try {
                    val repository = CategoryRepositoryImpl(reopened.categoryDao())
                    val categories = repository.observeCategories().first()
                    assertEquals(SystemCategoryIds.ALL + category.id, categories.map { it.id })
                    assertEquals(category, categories.last())
                    assertEquals("Camping", categories.last().name)
                    assertEquals(SystemCategoryIds.ALL, repository.observeSystemCategories().first().map { it.id })
                } finally {
                    reopened.close()
                }
            } finally {
                firstDatabase.close()
                context.deleteDatabase(databaseName)
            }
        }
}
