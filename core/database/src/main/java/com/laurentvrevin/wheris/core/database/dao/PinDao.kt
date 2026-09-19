package com.laurentvrevin.wheris.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {
    @Query("SELECT * FROM pins ORDER BY createdAtEpochMillis DESC")
    fun observePins(): Flow<List<PinEntity>>

    @Query("SELECT * FROM pins WHERE id = :pinId")
    fun observePin(pinId: String): Flow<PinEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPin(pin: PinEntity)
}
