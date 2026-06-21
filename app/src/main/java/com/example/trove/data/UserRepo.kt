package com.example.trove.data

import com.example.trove.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepo {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUser(uid: String): User? {
        val document = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return if (document.exists()) {
            document.toObject(User::class.java)
        } else {
            null
        }
    }

    suspend fun saveUser(user: User) {
        require(user.uid.isNotBlank()) {
            "User UID cannot be empty"
        }

        firestore
            .collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }
}