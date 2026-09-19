package com.laurentvrevin.wheris.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pins",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["categoryId"]),
    ],
)
data class PinEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val categoryId: String,
    val accuracyMeters: Float?,
    val altitudeMeters: Double?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
