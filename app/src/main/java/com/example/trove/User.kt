package com.example.trove

import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val username: String = "",
    val name: String = "",
    val bio: String = "",
    val countriesList: MutableList<String> = mutableListOf(),
    val followers: Int = 0,
    val numJournals: Int = 0,
    val likes: Int = 0,
    val friends: MutableList<String> = mutableListOf()
)

val UserSaver = mapSaver(
    save = { user ->
        mapOf(
            "username" to user.username,
            "name" to user.name,
            "bio" to user.bio,
            "countriesList" to user.countriesList,
            "followers" to user.followers,
            "numJournals" to user.numJournals
        )
    },
    restore = { map ->
        User(
            username = map["username"] as String,
            name = map["name"] as String,
            bio = map["bio"] as String,
            countriesList = map["countriesList"] as MutableList<String>,
            followers = map["followers"] as Int,
            numJournals = map["numJournals"] as Int
        )

    }
)

