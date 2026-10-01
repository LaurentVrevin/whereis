package com.laurentvrevin.wheris.core.database.dao

/** Database outcomes mapped to the domain contract by data. Unexpected errors still throw. */
enum class CategoryMutationStatus {
    SUCCESS,
    NOT_FOUND,
    SYSTEM_PROTECTED,
    IN_USE,
    REPLACEMENT_NOT_FOUND,
    SAME_CATEGORY,
}
