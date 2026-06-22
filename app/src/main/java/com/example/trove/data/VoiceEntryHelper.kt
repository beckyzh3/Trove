package com.example.trove.data

import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import java.util.UUID

object VoiceEntryHelper {
    fun createVoiceEntry(
        voiceMemoUrl: String,
        voiceDuration: String,
        title: String = "",
        dateRange: String = "Voice memo"
    ): TripEntry = TripEntry(
        id = UUID.randomUUID().toString(),
        type = EntryType.VOICE,
        dateRange = dateRange,
        text = title,
        voiceMemoUrl = voiceMemoUrl,
        voiceDuration = voiceDuration,
        timestamp = System.currentTimeMillis()
    )

    fun tripWithNewVoice(trip: Trip, entry: TripEntry): Trip =
        trip.copy(entries = trip.entries + entry)
}
