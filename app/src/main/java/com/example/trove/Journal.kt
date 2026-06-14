package com.example.trove

import androidx.compose.runtime.saveable.mapSaver

data class Journal(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val location: String = "",
    val entries: List<JournalEntry> = emptyList(),
    val routeSummary: String = "",
    val photoUris: List<String> = emptyList(),
    val likes: Int = 0
)


// unsure if i did this right so we leave it out for ui skeleton submission
/*
val JournalSaver = mapSaver(
    save = { journal ->
        mapOf(
            "id" to journal.id,
            "ownerId" to journal.ownerId,
            "name" to journal.name,
            "location" to journal.location,
            "entries" to journal.entries,
            "routeSummary" to journal.routeSummary,
            "photoUris" to journal.photoUris,
            "likes" to journal.likes
        )
    },
    restore = { map ->
        Journal(
            id = map["id"] as String,
            ownerId = map["ownerId"] as String,
            name = map["name"] as String,
            location = map["location"] as String,
            entries = map["entries"] as MutableList<JournalEntry>,
            routeSummary = map["routeSummary"] as String,
            photoUris = map["photoUris"] as MutableList<String>,
            likes = map["likes"] as Int
        )
    }
)

 */
