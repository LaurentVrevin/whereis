package com.laurentvrevin.wheris.core.model

@JvmInline
value class PinId(val value: String) {
    init {
        require(value.isNotBlank()) { "PinId cannot be empty or blank." }
    }
}
