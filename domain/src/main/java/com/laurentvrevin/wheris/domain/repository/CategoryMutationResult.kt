package com.laurentvrevin.wheris.domain.repository

/** Expected mutation outcomes, independent of the persistence implementation. */
enum class CategoryMutationResult {
    SUCCESS,
    NOT_FOUND,
    SYSTEM_PROTECTED,
    IN_USE,
    REPLACEMENT_NOT_FOUND,
    SAME_CATEGORY,
    TECHNICAL_FAILURE,
}
