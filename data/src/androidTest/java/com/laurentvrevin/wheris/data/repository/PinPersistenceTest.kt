package com.laurentvrevin.wheris.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PhotoReference
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PinPersistenceTest {
    @Test
    fun canonicalPlainAndEnrichedCreationSurviveRepositoryObservationAndDatabaseReopen() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val databaseName = "pin-details-v4-test.db"
            context.deleteDatabase(databaseName)

            fun openDatabase(): WherisDatabase =
                Room.databaseBuilder(context, WherisDatabase::class.java, databaseName)
                    .addCallback(WherisDatabase.getCallback()).build()
            var database = openDatabase()
            try {
                val repository = PinRepositoryImpl(database.pinDao(), FakePhotoStorage())
                val location = UserLocation(GeoPoint(10.0, 20.0), 7f, 35.0, 1000L)
                val plain = CreatePinUseCase(repository, { PinId("plain") }, { 2000L })(location, SystemCategoryIds.OTHER)
                val enriched =
                    CreatePinUseCase(repository, { PinId("enriched") }, { 3000L })(
                        location,
                        SystemCategoryIds.CAR,
                        "  Café 東京  ",
                        "  Note\n🌲  ",
                        true,
                        PhotoReference("photo-42"),
                    )
                assertFalse(plain.isFavorite)
                assertNull(plain.photoReference)
                assertNull(plain.name)
                assertNull(plain.note)
                assertEquals("Café 東京", enriched.name)
                assertEquals("  Note\n🌲  ", enriched.note)
                assertEquals(enriched, repository.observePin(enriched.id).first())
                assertEquals(listOf(enriched, plain), repository.observePins().first())
                assertEquals(4, database.openHelper.writableDatabase.version)
                database.close()
                database = openDatabase()
                val reopened = PinRepositoryImpl(database.pinDao(), FakePhotoStorage())
                assertEquals(listOf(enriched, plain), reopened.observePins().first())
                assertEquals(enriched, reopened.observePin(enriched.id).first())
                assertEquals(plain, reopened.observePin(plain.id).first())
                database.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
            } finally {
                database.close()
                context.deleteDatabase(databaseName)
            }
        }
}
