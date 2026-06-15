package com.example.trove

import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Serializable

@Serializable
data class Journal(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerProfilePicture: String = "",
    val name: String = "",
    val location: String = "",
    val entries: List<JournalEntry> = emptyList(),
    val routeSummary: String = "",
    val photoUris: List<String> = emptyList(),
    val likes: Int = 0,
    val isPublic: Boolean = true,
    val theme: String = "Classic"
)

val JournalSaver = mapSaver(
    save = { journal ->
        mapOf(
            "id" to journal.id,
            "ownerId" to journal.ownerId,
            "ownerName" to journal.ownerName,
            "ownerProfilePicture" to journal.ownerProfilePicture,
            "name" to journal.name,
            "location" to journal.location,
            "entries" to journal.entries.map { entry ->
                mapOf("dateRange" to entry.dateRange, "text" to entry.text)
            },
            "routeSummary" to journal.routeSummary,
            "photoUris" to journal.photoUris,
            "likes" to journal.likes,
            "isPublic" to journal.isPublic,
            "theme" to journal.theme
        )
    },
    restore = { map ->
        val entriesList = (map["entries"] as? List<*>)?.mapNotNull {
            val entryMap = it as? Map<*, *>
            if (entryMap != null) {
                JournalEntry(
                    dateRange = entryMap["dateRange"] as? String ?: "",
                    text = entryMap["text"] as? String ?: ""
                )
            } else null
        } ?: emptyList()

        Journal(
            id = map["id"] as? String ?: "",
            ownerId = map["ownerId"] as? String ?: "",
            ownerName = map["ownerName"] as? String ?: "",
            ownerProfilePicture = map["ownerProfilePicture"] as? String ?: "",
            name = map["name"] as? String ?: "",
            location = map["location"] as? String ?: "",
            entries = entriesList,
            routeSummary = map["routeSummary"] as? String ?: "",
            photoUris = (map["photoUris"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            likes = map["likes"] as? Int ?: 0,
            isPublic = map["isPublic"] as? Boolean ?: true,
            theme = map["theme"] as? String ?: "Classic"
        )
    }
)
