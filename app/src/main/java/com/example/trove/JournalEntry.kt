package com.example.trove

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: String = "",
    val dateRange: String = "",
    val text: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val photoUrl: String = "",
    val timestamp: Long = 0L
)
