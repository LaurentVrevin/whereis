package com.laurentvrevin.wheris.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val isSystem: Boolean,
    val name: String? = null,
    val iconKey: String? = null,
    val colorKey: String? = null,
    val createdAtEpochMillis: Long? = null,
) {
    init {
        // Validate new writes and hydrated rows at the data boundary as well as in the domain.
        Category(
            id = CategoryId(id),
            isSystem = isSystem,
            name = name,
            iconKey = iconKey?.let(CategoryIconKey::fromValue),
            colorKey = colorKey?.let(CategoryColorKey::fromValue),
            createdAtEpochMillis = createdAtEpochMillis,
        )
    }
}
