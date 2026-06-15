package com.example.trove

import androidx.compose.runtime.saveable.mapSaver
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String = "",
    val name: String = "",
    val bio: String = "",
    val email: String ="",
    val countriesList: MutableList<String> = mutableListOf(),
    val friends: MutableList<String> = mutableListOf(),
    val followers: Int = 0,
    val numJournals: Int = 0,
    val likes: Int = 0,
)

val UserSaver = mapSaver(
    save = { user ->
        mapOf(
            "username" to user.username,
            "name" to user.name,
            "bio" to user.bio,
            "email" to user.email,
            "countriesList" to user.countriesList,
            "friends" to user.friends,
            "followers" to user.followers,
            "numJournals" to user.numJournals,
            "likes" to user.likes
        )
    },
    restore = { map ->
        User(
            username = map["username"] as? String ?: "",
            name = map["name"] as? String ?: "",
            bio = map["bio"] as? String ?: "",
            email = map["email"] as? String ?: "",
            countriesList = (map["countriesList"] as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            friends = (map["friends"] as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            followers = map["followers"] as? Int ?: 0,
            numJournals = map["numJournals"] as? Int ?: 0,
            likes = map["likes"] as? Int ?: 0
        )
    }
)
