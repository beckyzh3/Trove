package com.example.trove.data

import android.content.Context
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class TripRepository {
    private val db = FirebaseFirestore.getInstance()

    /** Firestore collection name kept as "journals" for team compatibility. */
    private val tripsCollection = "journals"

    suspend fun getTripsWithEntries(ownerId: String): List<Trip> {
        if (ownerId.isBlank()) return emptyList()

        val snapshot = db.collection(tripsCollection)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        val trips = snapshot.documents.map { doc -> doc.toTripWithEntries() }.toMutableList()

        val demoId = BundledTripMedia.DEMO_TRIP_ID
        if (trips.none { it.id == demoId }) {
            val demoRef = db.collection(tripsCollection).document(demoId)
            val demoSnapshot = demoRef.get().await()
            if (demoSnapshot.exists()) {
                val demoTrip = demoSnapshot.toTripWithEntries()
                if (demoTrip.ownerId == ownerId) {
                    trips.add(demoTrip)
                }
            }
        }

        return trips
    }

    suspend fun getPublicTrips(): List<Trip> {
        val snapshot = db.collection(tripsCollection)
            .whereEqualTo("isPublic", true)
            .get()
            .await()

        return snapshot.documents.map { doc -> doc.toTripWithEntries() }
    }

    suspend fun tripExists(tripId: String): Boolean {
        if (tripId.isBlank()) return false
        return db.collection(tripsCollection).document(tripId).get().await().exists()
    }

    suspend fun toggleLike(tripId: String, userId: String): Trip {
        require(tripId.isNotBlank() && userId.isNotBlank()) {
            "Trip id and user id are required"
        }

        val tripRef = db.collection(tripsCollection).document(tripId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(tripRef)
            if (!snapshot.exists()) {
                throw IllegalStateException("Trip not found")
            }

            val likedBy = (snapshot.get("likedBy") as? List<*>)
                ?.filterIsInstance<String>()
                ?.toMutableList()
                ?: mutableListOf()

            if (userId in likedBy) {
                likedBy.remove(userId)
            } else {
                likedBy.add(userId)
            }

            transaction.update(
                tripRef,
                mapOf(
                    "likedBy" to likedBy,
                    "likes" to likedBy.size
                )
            )
        }.await()

        val updated = tripRef.get().await()
        return updated.toTripWithEntries()
    }

    suspend fun saveTrip(trip: Trip): String {
        val tripRef = if (trip.id.isBlank()) {
            db.collection(tripsCollection).document()
        } else {
            db.collection(tripsCollection).document(trip.id)
        }

        tripRef.set(
            mapOf(
                "ownerId" to trip.ownerId,
                "ownerName" to trip.ownerName,
                "ownerProfilePicture" to trip.ownerProfilePicture,
                "name" to trip.name,
                "location" to trip.location,
                "tripDates" to trip.tripDates,
                "routeSummary" to trip.routeSummary,
                "coverPhotoUrl" to trip.coverPhotoUrl,
                "distanceKm" to trip.distanceKm,
                "likes" to trip.likes,
                "likedBy" to trip.likedBy,
                "isPublic" to trip.isPublic,
                "theme" to trip.theme,
                "photoUris" to trip.photoUris
            ),
            SetOptions.merge()
        ).await()

        trip.entries.forEach { entry ->
            val entryRef = if (entry.id.isBlank()) {
                tripRef.collection("entries").document()
            } else {
                tripRef.collection("entries").document(entry.id)
            }
            entryRef.set(entry.copy(id = entryRef.id)).await()
        }

        return tripRef.id
    }

    suspend fun clearTripEntries(tripId: String) {
        val snapshot = db.collection(tripsCollection)
            .document(tripId)
            .collection("entries")
            .get()
            .await()
        snapshot.documents.forEach { it.reference.delete().await() }
    }

    /** Copies photos/voice to internal storage, then saves trip metadata + local paths to Firestore. */
    suspend fun saveTripWithLocalMedia(context: Context, trip: Trip): String {
        val tripId = trip.id.ifBlank {
            db.collection(tripsCollection).document().id
        }

        val persistedEntries = trip.entries.map { entry ->
            var updated = entry
            if (entry.photoUrl.isLocalMediaUri()) {
                updated = updated.copy(
                    photoUrl = LocalMediaStorage.persistPhoto(
                        context,
                        entry.photoUrl,
                        tripId,
                        LocalMediaStorage.uniquePhotoName(entry.id.ifBlank { "entry" })
                    )
                )
            }
            if (updated.voiceMemoUrl.isLocalMediaUri()) {
                updated = updated.copy(
                    voiceMemoUrl = LocalMediaStorage.persistVoice(
                        context,
                        updated.voiceMemoUrl,
                        tripId,
                        LocalMediaStorage.uniqueVoiceName(entry.id.ifBlank { "entry" })
                    )
                )
            }
            updated
        }

        val persistedPhotoUris = trip.photoUris.map { uri ->
            if (uri.isLocalMediaUri()) {
                LocalMediaStorage.persistPhoto(
                    context,
                    uri,
                    tripId,
                    LocalMediaStorage.uniquePhotoName("gallery")
                )
            } else {
                uri
            }
        }

        var coverPhotoUrl = trip.coverPhotoUrl
        if (coverPhotoUrl.isLocalMediaUri()) {
            coverPhotoUrl = LocalMediaStorage.persistPhoto(
                context,
                coverPhotoUrl,
                tripId,
                LocalMediaStorage.uniquePhotoName("cover")
            )
        } else if (coverPhotoUrl.isBlank()) {
            coverPhotoUrl = persistedEntries
                .firstOrNull { it.photoUrl.isNotBlank() }
                ?.photoUrl
                ?: persistedPhotoUris.firstOrNull().orEmpty()
        }

        return saveTrip(
            trip.copy(
                id = tripId,
                entries = persistedEntries,
                photoUris = persistedPhotoUris,
                coverPhotoUrl = coverPhotoUrl
            )
        )
    }

    private suspend fun DocumentSnapshot.toTripWithEntries(): Trip {
        val entries = reference.collection("entries")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(TripEntry::class.java)?.copy(id = it.id) }

        val likedBy = (get("likedBy") as? List<*>)
            ?.filterIsInstance<String>()
            ?: emptyList()

        return (toObject(Trip::class.java) ?: Trip()).copy(
            id = id,
            entries = entries,
            likedBy = likedBy,
            likes = likedBy.size
        )
    }
}
