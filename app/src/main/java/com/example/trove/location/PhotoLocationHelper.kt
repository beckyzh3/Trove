package com.example.trove.location

import android.content.Context
import android.media.ExifInterface
import android.net.Uri

object PhotoLocationHelper {
    /** Reads GPS from photo EXIF, if the image was geotagged. */
    fun getLatLng(context: Context, uri: Uri): Pair<Double, Double>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    latLong[0].toDouble() to latLong[1].toDouble()
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
