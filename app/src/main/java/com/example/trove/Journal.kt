package com.example.trove

data class Journal(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val location: String = "",
    val entries: List<JournalEntry> = emptyList(),
    val routeSummary: String = "",
    val photoUris: List<String> = emptyList(),
    val likes: Int = 0
)
