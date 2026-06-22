package com.example.trove.data

import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry

/**
 * Bootstraps demo trip metadata in Firestore for the logged-in user.
 * Each user gets their own Tokyo trip (auto-generated doc id). Paris is seeded once globally.
 * Photos and voice stay in app assets — [BundledTripMedia] / [BundledExploreTrips] attach them at display time.
 */
object DemoTripSeeder {
    suspend fun ensureDemoTrips(
        repository: TripRepository,
        ownerId: String,
        ownerName: String
    ) {
        if (ownerId.isBlank()) return

        val userTrips = runCatching {
            repository.getTripsWithEntries(ownerId)
        }.getOrDefault(emptyList())

        if (userTrips.none { BundledTripMedia.isBundledDemoTrip(it) }) {
            // Blank id → Firestore auto-id so each user gets their own Tokyo doc.
            repository.saveTrip(buildTokyoTrip(ownerId, ownerName, tripId = ""))
        }

        if (!repository.tripExists(BundledExploreTrips.PARIS_TRIP_ID)) {
            repository.saveTrip(buildParisTrip())
        }
    }

    /** Local fallback so the Tokyo demo always appears even if Firestore read fails. */
    fun mergeLocalDemoTrip(
        trips: List<Trip>,
        ownerId: String,
        ownerName: String
    ): List<Trip> {
        if (ownerId.isBlank()) return trips
        if (trips.any { BundledTripMedia.isBundledDemoTrip(it) }) return trips
        return trips + buildTokyoTrip(ownerId, ownerName, tripId = "")
    }

    fun buildTokyoTrip(
        ownerId: String,
        ownerName: String,
        tripId: String = ""
    ): Trip = Trip(
        id = tripId,
        ownerId = ownerId,
        ownerName = ownerName,
        name = BundledTripMedia.DEMO_TRIP_NAME,
        location = "Tokyo, Japan",
        tripDates = BundledTripMedia.DEMO_TRIP_DATES,
        routeSummary = "Shinjuku → Asakusa → Kawaguchiko",
        distanceKm = "6.2 km",
        isPublic = true,
        theme = "Vintage",
        entries = tokyoEntries()
    )

    private fun buildParisTrip(): Trip = Trip(
        id = BundledExploreTrips.PARIS_TRIP_ID,
        ownerId = BundledExploreTrips.ALICE_UID,
        ownerName = BundledExploreTrips.demoFriendUser.name,
        name = "Paris Getaway",
        location = "Paris, France",
        tripDates = "Oct 3–9, 2024",
        routeSummary = "Montmartre → Louvre → Eiffel Tower",
        distanceKm = "4.8 km",
        isPublic = true,
        theme = "Autumn Breeze",
        entries = parisEntries()
    )

    private fun tokyoEntries(): List<TripEntry> = listOf(
        TripEntry(
            id = "tokyo-voice-1",
            type = EntryType.VOICE,
            dateRange = "Day 1",
            text = "First impressions of Shinjuku",
            voiceDuration = "0:02",
            timestamp = 1L
        ),
        TripEntry(
            id = "tokyo-voice-2",
            type = EntryType.VOICE,
            dateRange = "Day 2",
            text = "Rain on the temple steps",
            voiceDuration = "0:03",
            timestamp = 2L
        ),
        TripEntry(
            id = "tokyo-voice-3",
            type = EntryType.VOICE,
            dateRange = "Day 3",
            text = "Finding ramen at midnight",
            voiceDuration = "0:04",
            timestamp = 3L
        ),
        TripEntry(
            id = "tokyo-text-1",
            type = EntryType.TEXT,
            dateRange = "Day 1 — May 12",
            timeLabel = "8:34 PM",
            text = "Landed at Narita feeling both exhausted and buzzing with excitement.",
            timestamp = 4L
        ),
        TripEntry(
            id = "tokyo-photo-1",
            type = EntryType.PHOTO,
            dateRange = "Day 2",
            text = "Senso-ji Temple",
            latitude = 35.7119,
            longitude = 139.7967,
            timestamp = 5L
        ),
        TripEntry(
            id = "tokyo-photo-2",
            type = EntryType.PHOTO,
            dateRange = "Day 3",
            text = "Shinjuku at night",
            latitude = 35.6595,
            longitude = 139.7005,
            timestamp = 6L
        ),
        TripEntry(
            id = "tokyo-photo-3",
            type = EntryType.PHOTO,
            dateRange = "Day 4",
            text = "Mount Fuji from Kawaguchiko",
            latitude = 35.5170,
            longitude = 138.7570,
            timestamp = 7L
        )
    )

    private fun parisEntries(): List<TripEntry> = listOf(
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
}
