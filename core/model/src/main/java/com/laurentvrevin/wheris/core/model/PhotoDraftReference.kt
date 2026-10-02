package com.laurentvrevin.wheris.core.model

/** Opaque owned draft handle, never persisted in a Pin or interpreted as a path. */
@JvmInline
value class PhotoDraftReference(val value: String) {
    init {
        require(value.matches(Regex("[A-Za-z0-9][A-Za-z0-9_-]*")))
    }
}
