package com.laurentvrevin.wheris.data.mapper

import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.Pin
import com.laurentvrevin.wheris.core.model.PinId

fun PinEntity.toDomain(): Pin {
    return Pin(
        id = PinId(id),
        position = GeoPoint(latitude, longitude),
        categoryId = CategoryId(categoryId),
        accuracyMeters = accuracyMeters,
        altitudeMeters = altitudeMeters,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
}

fun Pin.toEntity(): PinEntity {
    return PinEntity(
        id = id.value,
        latitude = position.latitude,
        longitude = position.longitude,
        categoryId = categoryId.value,
        accuracyMeters = accuracyMeters,
        altitudeMeters = altitudeMeters,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
}
