package com.example.trove.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

object LocationHelper {

    /** Downtown SF — used only on emulators in debug when GPS is unavailable. */
    val EMULATOR_SF = LatLng(37.7879, -122.4074)

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.contains("emulator")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HARDWARE.contains("ranchu")
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLatLng(context: Context): LatLng? {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cancellationToken = CancellationTokenSource()

        val current = runCatching {
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationToken.token)
                .await()
        }.getOrNull()

        if (current != null) {
            return LatLng(current.latitude, current.longitude)
        }

        val last = runCatching { client.lastLocation.await() }.getOrNull()
        return last?.let { LatLng(it.latitude, it.longitude) }
    }
}
