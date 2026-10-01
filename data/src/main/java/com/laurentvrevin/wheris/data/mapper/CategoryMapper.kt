package com.laurentvrevin.wheris.data.mapper

import com.laurentvrevin.wheris.core.database.entity.CategoryEntity
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId

fun CategoryEntity.toDomain(): Category =
    Category(
        id = CategoryId(id),
        isSystem = isSystem,
        name = name,
        iconKey = iconKey?.let(CategoryIconKey::fromValue),
        colorKey = colorKey?.let(CategoryColorKey::fromValue),
        createdAtEpochMillis = createdAtEpochMillis,
    )
