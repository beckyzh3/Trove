package com.example.trove

import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerProfilePicture: String = "",
    val name: String = "",
    val location: String = "",
    val tripDates: String = "",
    val entries: List<TripEntry> = emptyList(),
    val routeSummary: String = "",
    val photoUris: List<String> = emptyList(),
    val coverPhotoUrl: String = "",
    val distanceKm: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val theme: String = "Classic"
)

val TripSaver = mapSaver(
    save = { trip ->
        mapOf(
            "id" to trip.id,
            "ownerId" to trip.ownerId,
            "ownerName" to trip.ownerName,
            "ownerProfilePicture" to trip.ownerProfilePicture,
            "name" to trip.name,
            "location" to trip.location,
            "tripDates" to trip.tripDates,
            "entries" to trip.entries.map { entry ->
                mapOf(
                    "id" to entry.id,
                    "type" to entry.type.name,
                    "dateRange" to entry.dateRange,
                    "timeLabel" to entry.timeLabel,
                    "text" to entry.text,
                    "latitude" to entry.latitude,
                    "longitude" to entry.longitude,
                    "photoUrl" to entry.photoUrl,
                    "voiceMemoUrl" to entry.voiceMemoUrl,
                    "voiceDuration" to entry.voiceDuration,
                    "timestamp" to entry.timestamp
                )
            },
            "routeSummary" to trip.routeSummary,
            "photoUris" to trip.photoUris,
            "coverPhotoUrl" to trip.coverPhotoUrl,
            "distanceKm" to trip.distanceKm,
            "likes" to trip.likes,
            "likedBy" to trip.likedBy,
            "isPublic" to trip.isPublic,
            "theme" to trip.theme
        )
    },
    restore = { map ->
        val entriesList = (map["entries"] as? List<*>)?.mapNotNull {
            val entryMap = it as? Map<*, *>
            if (entryMap != null) {
                TripEntry(
                    id = entryMap["id"] as? String ?: "",
                    type = (entryMap["type"] as? String)?.let {
                        runCatching { EntryType.valueOf(it) }.getOrDefault(EntryType.TEXT)
                    } ?: EntryType.TEXT,
                    dateRange = entryMap["dateRange"] as? String ?: "",
                    timeLabel = entryMap["timeLabel"] as? String ?: "",
                    text = entryMap["text"] as? String ?: "",
                    latitude = entryMap["latitude"] as? Double ?: 0.0,
                    longitude = entryMap["longitude"] as? Double ?: 0.0,
                    photoUrl = entryMap["photoUrl"] as? String ?: "",
                    voiceMemoUrl = entryMap["voiceMemoUrl"] as? String ?: "",
                    voiceDuration = entryMap["voiceDuration"] as? String ?: "",
                    timestamp = entryMap["timestamp"] as? Long ?: 0L
                )
            } else null
        } ?: emptyList()

        Trip(
            id = map["id"] as? String ?: "",
            ownerId = map["ownerId"] as? String ?: "",
            ownerName = map["ownerName"] as? String ?: "",
            ownerProfilePicture = map["ownerProfilePicture"] as? String ?: "",
            name = map["name"] as? String ?: "",
            location = map["location"] as? String ?: "",
            tripDates = map["tripDates"] as? String ?: "",
            entries = entriesList,
            routeSummary = map["routeSummary"] as? String ?: "",
            photoUris = (map["photoUris"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            coverPhotoUrl = map["coverPhotoUrl"] as? String ?: "",
            distanceKm = map["distanceKm"] as? String ?: "",
            likes = map["likes"] as? Int ?: 0,
            likedBy = (map["likedBy"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            isPublic = map["isPublic"] as? Boolean ?: true,
            theme = map["theme"] as? String ?: "Classic"
        )
    }
)
