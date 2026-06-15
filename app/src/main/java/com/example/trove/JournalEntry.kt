package com.example.trove

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val dateRange: String = "",
    val text: String = ""
)
