package com.laurentvrevin.wheris.core.model

/** Initial subset of the named category accents in docs/design/02_DESIGN_TOKENS.md. */
enum class CategoryColorKey(val value: String) {
    ORANGE("category/orange"),
    GREEN("category/green"),
    BLUE("category/blue"),
    ;

    companion object {
        fun fromValue(value: String): CategoryColorKey = entries.first { it.value == value }
    }
}
