package com.example.trove

import kotlinx.serialization.Serializable

/** What kind of memory this stop represents inside a trip. */
@Serializable
enum class EntryType {
    TEXT,
    VOICE,
    PHOTO,
    LOCATION
}
