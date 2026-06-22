package com.example.trove

import kotlinx.serialization.Serializable

@Serializable
data class TripEntry(
    val id: String = "",
    val type: EntryType = EntryType.TEXT,
    val dateRange: String = "",
    val timeLabel: String = "",
    val text: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val photoUrl: String = "",
    val voiceMemoUrl: String = "",
    val voiceDuration: String = "",
    val timestamp: Long = 0L
)
