package com.laurentvrevin.wheris.core.model

data class Category(
    val id: CategoryId,
    val isSystem: Boolean,
    val name: String? = null,
    val iconKey: CategoryIconKey? = null,
    val colorKey: CategoryColorKey? = null,
    val createdAtEpochMillis: Long? = null,
) {
    init {
        if (isSystem) {
            require(id in SystemCategoryIds.ALL) { "Unknown system category." }
            require(name == null && iconKey == null && colorKey == null && createdAtEpochMillis == null)
        } else {
            require(id !in SystemCategoryIds.ALL) { "System category IDs are reserved." }
            require(!name.isNullOrBlank() && name == name.trim()) { "Custom name must be trimmed and nonblank." }
            require(iconKey != null && colorKey != null && createdAtEpochMillis != null)
        }
    }
}
