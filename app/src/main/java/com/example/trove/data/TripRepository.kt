package com.example.trove.data

import android.content.Context
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TripRepository {
    private val db = FirebaseFirestore.getInstance()
    private val photoStorage = PhotoStorageRepository()

    /** Firestore collection name kept as "journals" for team compatibility. */
    private val tripsCollection = "journals"

    suspend fun getTripsWithEntries(ownerId: String): List<Trip> {
        val snapshot = db.collection(tripsCollection)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { doc -> doc.toTripWithEntries() }
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
                "isPublic" to trip.isPublic,
                "theme" to trip.theme,
                "photoUris" to trip.photoUris
            )
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

    /** Uploads any local photo URIs to Firebase Storage, then saves trip metadata to Firestore. */
    suspend fun saveTripWithPhotos(context: Context, trip: Trip): String {
        val tripId = trip.id.ifBlank {
            db.collection(tripsCollection).document().id
        }

        val uploadedEntries = trip.entries.map { entry ->
            if (entry.photoUrl.isLocalPhotoUri()) {
                val fileName = PhotoStorageRepository.uniqueFileName(entry.id.ifBlank { "entry" })
                entry.copy(
                    photoUrl = photoStorage.uploadFromUriString(
                        entry.photoUrl, tripId, fileName, context
                    )
                )
            } else {
                entry
            }
        }

        val uploadedPhotoUris = trip.photoUris.map { uri ->
            if (uri.isLocalPhotoUri()) {
                photoStorage.uploadFromUriString(
                    uri, tripId, PhotoStorageRepository.uniqueFileName("gallery"), context
                )
            } else {
                uri
            }
        }

        var coverPhotoUrl = trip.coverPhotoUrl
        if (coverPhotoUrl.isLocalPhotoUri()) {
            coverPhotoUrl = photoStorage.uploadFromUriString(
                coverPhotoUrl, tripId, PhotoStorageRepository.uniqueFileName("cover"), context
            )
        } else if (coverPhotoUrl.isBlank()) {
            coverPhotoUrl = uploadedEntries
                .firstOrNull { it.photoUrl.isRemotePhotoUrl() }
                ?.photoUrl
                ?: uploadedPhotoUris.firstOrNull().orEmpty()
        }

        return saveTrip(
            trip.copy(
                id = tripId,
                entries = uploadedEntries,
                photoUris = uploadedPhotoUris,
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

        return (toObject(Trip::class.java) ?: Trip()).copy(
            id = id,
            entries = entries
        )
    }
}
