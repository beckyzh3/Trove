package com.example.trove.data

import android.content.Context
import android.net.Uri
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.example.trove.location.LocationHelper
import com.example.trove.location.PhotoLocationHelper
import java.util.UUID

object PhotoEntryHelper {
    suspend fun createPhotoEntry(
        context: Context,
        uri: Uri,
        dateRange: String = "Photo"
    ): TripEntry {
        val exifCoords = PhotoLocationHelper.getLatLng(context, uri)
        var latitude = exifCoords?.first ?: 0.0
        var longitude = exifCoords?.second ?: 0.0

        if (exifCoords == null) {
            val current = LocationHelper.getCurrentLatLng(context)
            if (current != null) {
                latitude = current.latitude
                longitude = current.longitude
            } else if (LocationHelper.isEmulator()) {
                latitude = LocationHelper.EMULATOR_SF.latitude
                longitude = LocationHelper.EMULATOR_SF.longitude
            }
        }

        return TripEntry(
            id = UUID.randomUUID().toString(),
            type = EntryType.PHOTO,
            dateRange = dateRange,
            photoUrl = uri.toString(),
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis()
        )
    }

    fun tripWithNewPhoto(trip: Trip, entry: TripEntry): Trip =
        trip.copy(entries = trip.entries + entry)
}
