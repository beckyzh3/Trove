package com.example.trove

fun Trip.mapStops(): List<TripEntry> =
    entries.filter { it.latitude != 0.0 && it.longitude != 0.0 }

fun Trip.textEntries(): List<TripEntry> =
    entries.filter { it.type == EntryType.TEXT && it.text.isNotBlank() }

fun Trip.voiceMemos(): List<TripEntry> =
    entries.filter { it.type == EntryType.VOICE }

fun Trip.locationStops(): List<TripEntry> =
    entries.filter { it.type == EntryType.LOCATION && it.latitude != 0.0 && it.longitude != 0.0 }

fun Trip.photoEntries(): List<TripEntry> =
    entries.filter { it.type == EntryType.PHOTO || it.photoUrl.isNotBlank() }

fun Trip.allPhotoUrls(): List<String> {
    val fromEntries = photoEntries().mapNotNull { it.photoUrl.takeIf { url -> url.isNotBlank() } }
    return (photoUris + fromEntries).distinct()
}
