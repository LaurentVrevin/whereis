package com.laurentvrevin.wheris.core.model

@JvmInline
value class CategoryId(val value: String) {
    init {
        require(value.isNotBlank()) { "CategoryId cannot be empty or blank." }
    }
}
