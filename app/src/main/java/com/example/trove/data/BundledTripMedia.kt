package com.example.trove.data

import android.content.Context
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry

/**
 * Bundled photos/voice shipped in assets for the demo Tokyo trip.
 * Applied at display time — Firestore only stores trip metadata.
 */
object BundledTripMedia {
    const val DEMO_TRIP_ID = "demo-trip-tokyo"
    const val DEMO_TRIP_NAME = "Lost in Tokyo's Backstreets"
    const val DEMO_TRIP_DATES = "May 12–19, 2024"

    fun isBundledDemoTrip(trip: Trip): Boolean =
        trip.id == DEMO_TRIP_ID ||
            (trip.name == DEMO_TRIP_NAME && trip.tripDates == DEMO_TRIP_DATES)

    private val tokyoPhotoAssets = listOf(
        "samples/photos/tokyo_cover.webp" to "tokyo_cover.webp",
        "samples/photos/tokyo_temple.jpg" to "tokyo_temple.jpg",
        "samples/photos/tokyo_street.jpg" to "tokyo_street.jpg",
        "samples/photos/tokyo_fuji.jpg" to "tokyo_fuji.jpg",
        "samples/photos/tokyo_neon.jpg" to "tokyo_neon.jpg"
    )

    private val tokyoVoiceAssets = listOf(
        "samples/voice/memo_1.wav" to "memo_1.wav",
        "samples/voice/memo_2.wav" to "memo_2.wav",
        "samples/voice/memo_3.wav" to "memo_3.wav"
    )

    fun apply(context: Context, trip: Trip): Trip {
        if (!isBundledDemoTrip(trip)) return trip

        val storageId = mediaStorageId(trip)
        val photos = tokyoPhotoAssets.map { (asset, fileName) ->
            LocalMediaStorage.copyAssetPhoto(context, asset, storageId, fileName)
        }
        val voices = tokyoVoiceAssets.map { (asset, fileName) ->
            LocalMediaStorage.copyAssetVoice(context, asset, storageId, fileName)
        }

        var photoIndex = 1
        var voiceIndex = 0
        val updatedEntries = trip.entries.map { entry ->
            when (entry.type) {
                EntryType.PHOTO -> {
                    val url = photos.getOrElse(photoIndex) { photos.last() }
                    photoIndex++
                    entry.copy(photoUrl = url)
                }
                EntryType.VOICE -> {
                    val url = voices.getOrElse(voiceIndex) { "" }
                    voiceIndex++
                    entry.copy(voiceMemoUrl = url)
                }
                else -> entry
            }
        }

        val entriesWithMedia = updatedEntries.ifEmpty {
            defaultTokyoEntries(photos, voices)
        }

        return trip.copy(
            coverPhotoUrl = photos.first(),
            photoUris = photos.drop(1),
            entries = entriesWithMedia
        )
    }

    /** Per-user/per-trip folder for copied assets (supports auto-generated Firestore ids). */
    internal fun mediaStorageId(trip: Trip): String =
        trip.id.takeIf { it.isNotBlank() } ?: "demo-${trip.ownerId}"

    private fun defaultTokyoEntries(photos: List<String>, voices: List<String>): List<TripEntry> =
        listOf(
            TripEntry(
                id = "tokyo-voice-1",
                type = EntryType.VOICE,
                dateRange = "Day 1",
                text = "First impressions of Shinjuku",
                voiceMemoUrl = voices.getOrElse(0) { "" },
                voiceDuration = "0:02",
                timestamp = 1L
            ),
            TripEntry(
                id = "tokyo-voice-2",
                type = EntryType.VOICE,
                dateRange = "Day 2",
                text = "Rain on the temple steps",
                voiceMemoUrl = voices.getOrElse(1) { "" },
                voiceDuration = "0:03",
                timestamp = 2L
            ),
            TripEntry(
                id = "tokyo-voice-3",
                type = EntryType.VOICE,
                dateRange = "Day 3",
                text = "Finding ramen at midnight",
                voiceMemoUrl = voices.getOrElse(2) { "" },
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
                photoUrl = photos.getOrElse(1) { "" },
                latitude = 35.7119,
                longitude = 139.7967,
                timestamp = 5L
            ),
            TripEntry(
                id = "tokyo-photo-2",
                type = EntryType.PHOTO,
                dateRange = "Day 3",
                text = "Shinjuku at night",
                photoUrl = photos.getOrElse(2) { "" },
                latitude = 35.6595,
                longitude = 139.7005,
                timestamp = 6L
            ),
            TripEntry(
                id = "tokyo-photo-3",
                type = EntryType.PHOTO,
                dateRange = "Day 4",
                text = "Mount Fuji from Kawaguchiko",
                photoUrl = photos.getOrElse(3) { "" },
                latitude = 35.5170,
                longitude = 138.7570,
                timestamp = 7L
            )
        )
}
