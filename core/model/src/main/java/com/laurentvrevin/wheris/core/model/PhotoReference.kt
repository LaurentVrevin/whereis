package com.laurentvrevin.wheris.core.model

/**
 * Opaque identifier of a permanent, application-owned local photo, never a draft/cache file.
 *
 * ASCII letters, digits, underscores and hyphens only, starting with a letter or digit.
 * This is neither a path nor a URI. Only the storage boundary may resolve it to a file.
 * Construction validates the identifier, not the existence or ownership of a physical file.
 * See docs/engineering/LOCAL_PHOTO_LIFECYCLE.md for the persistence/cleanup contract.
 */
@JvmInline
value class PhotoReference(val value: String) {
    init {
        require(value.matches(Regex("[A-Za-z0-9][A-Za-z0-9_-]*"))) {
            "PhotoReference must be an opaque local photo identifier."
        }
    }
}
