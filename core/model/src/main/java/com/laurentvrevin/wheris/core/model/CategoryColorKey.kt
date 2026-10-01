package com.laurentvrevin.wheris.core.model

/** Stable keys for the approved category palette in docs/design/03_DESIGN_COMPONENTS.md. */
enum class CategoryColorKey(val value: String) {
    ORANGE("category/orange"),
    AMBER("category/amber"),
    YELLOW("category/yellow"),
    GREEN("category/green"),
    TEAL("category/teal"),
    BLUE("category/blue"),
    INDIGO("category/indigo"),
    PURPLE("category/purple"),
    PINK("category/pink"),
    RED("category/red"),
    BROWN("category/brown"),
    SLATE("category/slate"),
    ;

    companion object {
        fun fromValue(value: String): CategoryColorKey = entries.first { it.value == value }
    }
}
