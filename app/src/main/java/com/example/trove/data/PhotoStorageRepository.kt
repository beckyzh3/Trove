package com.example.trove.data

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PhotoStorageRepository {
    private val storage = FirebaseStorage.getInstance()

    /** Storage path mirrors Firestore collection name for team consistency. */
    suspend fun uploadTripPhoto(
        localUri: Uri,
        tripId: String,
        fileName: String,
        context: Context
    ): String {
        val ref = storage.reference
            .child("journals")
            .child(tripId)
            .child("photos")
            .child(fileName)

        val metadata = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        context.contentResolver.openInputStream(localUri).use { stream ->
            requireNotNull(stream) { "Could not read photo from device" }
            ref.putStream(stream, metadata).await()
        }

        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadFromUriString(
        uriString: String,
        tripId: String,
        fileName: String,
        context: Context
    ): String {
        if (!uriString.isLocalPhotoUri()) return uriString
        return uploadTripPhoto(Uri.parse(uriString), tripId, fileName, context)
    }

    companion object {
        fun uniqueFileName(prefix: String = "photo"): String =
            "$prefix-${UUID.randomUUID()}.jpg"
    }
}
