package com.laurentvrevin.wheris.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("DELETE FROM pins WHERE id = :pinId")
    suspend fun deletePin(pinId: String)

    @Query("SELECT * FROM pins WHERE id = :pinId")
    suspend fun getPin(pinId: String): PinEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE id = :categoryId)")
    suspend fun categoryExists(categoryId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM pins WHERE photoReference = :photo AND id != :pinId)")
    suspend fun photoAttachedElsewhere(
        photo: String,
        pinId: String,
    ): Boolean

    @Query(
        "UPDATE pins SET categoryId = :categoryId, name = :name, note = :note, " +
            "isFavorite = :isFavorite, photoReference = :photoReference, updatedAtEpochMillis = :updatedAt WHERE id = :pinId",
    )
    suspend fun updateEditableRow(
        pinId: String,
        categoryId: String,
        name: String?,
        note: String?,
        isFavorite: Boolean,
        photoReference: String?,
        updatedAt: Long,
    ): Int

    /** Rechecks preflight assumptions under the same Room transaction as the single UPDATE. */
    @Transaction
    suspend fun mutatePin(
        pinId: String,
        categoryId: String,
        name: String?,
        note: String?,
        isFavorite: Boolean,
        photoReference: String?,
        expectedPhotoReference: String?,
        updatedAt: Long,
    ): PinMutationStatus {
        val current = getPin(pinId) ?: return PinMutationStatus.PIN_NOT_FOUND
        if (!categoryExists(categoryId)) return PinMutationStatus.CATEGORY_NOT_FOUND
        if (current.photoReference != expectedPhotoReference) return PinMutationStatus.CONFLICT
        if (photoReference != current.photoReference && photoReference != null && photoAttachedElsewhere(photoReference, pinId)) {
            return PinMutationStatus.PHOTO_ALREADY_ATTACHED
        }
        check(updateEditableRow(pinId, categoryId, name, note, isFavorite, photoReference, updatedAt) == 1)
        return PinMutationStatus.SUCCESS
    }
}
