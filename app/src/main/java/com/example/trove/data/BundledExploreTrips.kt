package com.example.trove.data

import android.content.Context
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.example.trove.User

/** Attaches bundled local photos/voice for Alice's Paris demo trip at display time. */
object BundledExploreTrips {
    const val ALICE_UID = "alice-uid"
    const val PARIS_TRIP_ID = "alice-journal-1"

    val demoFriendUser = User(
        uid = ALICE_UID,
        username = "alice",
        name = "Alice Smith",
        bio = "Collecting memories around the world.",
        countriesList = mutableListOf("France", "Italy"),
        friends = mutableListOf(),
        numJournals = 1,
        likes = 45
    )

    /** Profile for a friend trip owner; Alice uses the full demo profile. */
    fun friendProfile(ownerId: String, trips: List<Trip>): User? {
        if (ownerId.isBlank()) return null
        if (ownerId == ALICE_UID) return demoFriendUser
        val trip = trips.firstOrNull { it.ownerId == ownerId } ?: return null
        return User(
            uid = ownerId,
            username = trip.ownerName.lowercase().replace(" ", ""),
            name = trip.ownerName,
            profilePicture = trip.ownerProfilePicture
        )
    }

    private val parisPhotoAssets = listOf(
        "samples/photos/tokyo_temple.jpg" to "paris_cover.jpg",
        "samples/photos/tokyo_street.jpg" to "paris_2.jpg"
    )

    private val parisVoiceAsset = "samples/voice/memo_1.wav" to "paris_memo.wav"

    fun mergeWithFirestore(firestoreTrips: List<Trip>): List<Trip> {
        val knownIds = firestoreTrips.map { it.id }.toSet()
        return firestoreTrips + bundledFriendTrips().filter { it.id !in knownIds }
    }

    fun apply(context: Context, trip: Trip): Trip {
        if (trip.id != PARIS_TRIP_ID) return trip
        return applyParisMedia(context, trip)
    }

    fun applyTrip(context: Context, trip: Trip): Trip =
        applyAll(context, listOf(trip)).first()

    fun applyAll(context: Context, trips: List<Trip>): List<Trip> =
        trips.map { trip ->
            BundledTripMedia.apply(context, apply(context, trip))
        }

    private fun applyParisMedia(context: Context, trip: Trip): Trip {
        val photos = parisPhotoAssets.map { (asset, fileName) ->
            LocalMediaStorage.copyAssetPhoto(context, asset, PARIS_TRIP_ID, fileName)
        }
        val voice = LocalMediaStorage.copyAssetVoice(
            context,
            parisVoiceAsset.first,
            PARIS_TRIP_ID,
            parisVoiceAsset.second
        )

        var photoIndex = 1
        val entries = trip.entries.map { entry ->
            when (entry.type) {
                EntryType.PHOTO -> {
                    val url = photos.getOrElse(photoIndex) { photos.last() }
                    photoIndex++
                    entry.copy(photoUrl = url)
                }
                EntryType.VOICE -> entry.copy(voiceMemoUrl = voice)
                else -> entry
            }
        }

        return trip.copy(
            coverPhotoUrl = photos.first(),
            photoUris = photos.drop(1),
            entries = entries
        )
    }

    private fun bundledFriendTrips(): List<Trip> = listOf(parisTrip())

    private fun parisTrip(): Trip = Trip(
        id = PARIS_TRIP_ID,
        ownerId = ALICE_UID,
        ownerName = demoFriendUser.name,
        name = "Paris Getaway",
        location = "Paris, France",
        tripDates = "Oct 3–9, 2024",
        routeSummary = "Montmartre → Louvre → Eiffel Tower",
        distanceKm = "4.8 km",
        isPublic = true,
        theme = "Autumn Breeze",
        entries = listOf(
            TripEntry(
                id = "paris-voice-1",
                type = EntryType.VOICE,
                dateRange = "Day 1",
                text = "Morning croissants in Montmartre",
                voiceDuration = "0:02",
                timestamp = 1L
            ),
            TripEntry(
                id = "paris-text-1",
                type = EntryType.TEXT,
                dateRange = "Day 1 — Oct 3",
                timeLabel = "10:15 AM",
                text = "The stairs up to Sacré-Cœur were worth every step.",
                timestamp = 2L
            ),
            TripEntry(
                id = "paris-photo-1",
                type = EntryType.PHOTO,
                dateRange = "Day 2",
                text = "Louvre courtyard",
                latitude = 48.8606,
                longitude = 2.3376,
                timestamp = 3L
            ),
            TripEntry(
                id = "paris-photo-2",
                type = EntryType.PHOTO,
                dateRange = "Day 3",
                text = "Eiffel Tower at dusk",
                latitude = 48.8584,
                longitude = 2.2945,
                timestamp = 4L
            )
        )
    )
}
