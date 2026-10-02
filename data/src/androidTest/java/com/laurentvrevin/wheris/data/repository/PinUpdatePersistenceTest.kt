package com.laurentvrevin.wheris.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.PinPhotoChange
import com.laurentvrevin.wheris.domain.PinUpdate
import com.laurentvrevin.wheris.domain.PinUpdateResult
import com.laurentvrevin.wheris.domain.usecase.CreatePinUseCase
import com.laurentvrevin.wheris.domain.usecase.UpdatePinUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PinUpdatePersistenceTest {
    @Test fun canonicalMutationSurvivesDatabaseReopenWithoutChangingGeographicMetadata() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val name = "pin-update-v4-test.db"
            context.deleteDatabase(name)

            fun open() =
                Room.databaseBuilder(context, WherisDatabase::class.java, name)
                    .addCallback(WherisDatabase.getCallback()).build()
            var db = open()
            try {
                val repository = PinRepositoryImpl(db.pinDao(), FakePhotoStorage())
                val original =
                    CreatePinUseCase(repository, { PinId("edit") }, { 2000L })(
                        UserLocation(GeoPoint(10.0, 20.0), 7f, 35.0, 1000L),
                        SystemCategoryIds.CAR,
                    )
                val update = PinUpdate(original.id, SystemCategoryIds.PARKING, "  Café 東京  ", "  Note\n🌲  ", true, PinPhotoChange.Keep)
                assertEquals(PinUpdateResult.SUCCESS, UpdatePinUseCase(repository) { 5000L }(update))
                val expected =
                    original.copy(
                        categoryId = SystemCategoryIds.PARKING,
                        name = "Café 東京",
                        note = "  Note\n🌲  ",
                        isFavorite = true,
                        updatedAtEpochMillis = 5000L,
                    )
                assertEquals(expected, repository.observePin(original.id).first())
                db.close()
                db = open()
                val reopened = PinRepositoryImpl(db.pinDao(), FakePhotoStorage())
                assertEquals(expected, reopened.observePin(original.id).first())
                assertEquals(PinUpdateResult.SUCCESS, UpdatePinUseCase(reopened) { 6000L }(update.copy(isFavorite = false)))
                assertEquals(expected.copy(isFavorite = false, updatedAtEpochMillis = 6000L), reopened.observePin(original.id).first())
                assertEquals(4, db.openHelper.writableDatabase.version)
                db.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
            } finally {
                db.close()
                context.deleteDatabase(name)
            }
        }
}
