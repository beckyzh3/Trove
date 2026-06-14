package com.example.trove

import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val username: String = "",
    val name: String = "",
    val bio: String = "",
    val countriesList: MutableList<String> = mutableListOf(),
    val friends: MutableList<String> = mutableListOf(),
    val numJournals: Int = 0,
    val likes: Int = 0,
)

val UserSaver = mapSaver(
    save = { user ->
        mapOf(
            "username" to user.username,
            "name" to user.name,
            "bio" to user.bio,
            "countriesList" to user.countriesList,
            "friends" to user.friends,
            "numJournals" to user.numJournals,
            "likes" to user.likes
        )
    },
    restore = { map ->
        User(
            username = map["username"] as String,
            name = map["name"] as String,
            bio = map["bio"] as String,
            countriesList = map["countriesList"] as MutableList<String>,
            friends = map["friends"] as MutableList<String>,
            numJournals = map["numJournals"] as Int,
            likes = map["likes"] as Int
        )
    }
)

