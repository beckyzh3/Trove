package com.example.trove.ui.screens

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.trove.TripEntry
import com.example.trove.location.LocationHelper
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(
    entries: List<TripEntry>,
    onPinClick: (TripEntry) -> Unit,
    /** One path per trip on the global map. When empty, all entries form a single path (trip detail). */
    tripPaths: List<List<TripEntry>> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var deviceLocation by remember { mutableStateOf<LatLng?>(null) }
    var useSimulatedLocation by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocationPermission =
            results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val isDebugBuild = remember {
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@LaunchedEffect

        val location = LocationHelper.getCurrentLatLng(context)
        deviceLocation = location

        // Debug emulator fallback: pretend you are in SF when GPS is unavailable.
        // Never saved to Firestore — only shown on your device.
        useSimulatedLocation =
            isDebugBuild &&
                LocationHelper.isEmulator() &&
                location == null
    }

    val pinEntries = remember(entries) {
        entries.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    }

    val myLocation = when {
        deviceLocation != null -> deviceLocation
        useSimulatedLocation -> LocationHelper.EMULATOR_SF
        else -> null
    }

    val mapCenter = myLocation
        ?: pinEntries.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
        ?: LocationHelper.EMULATOR_SF

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
    }

    val pathGroups = remember(pinEntries, tripPaths) {
        if (tripPaths.isNotEmpty()) {
            tripPaths.map { group ->
                group
                    .filter { it.latitude != 0.0 && it.longitude != 0.0 }
                    .sortedBy { it.timestamp }
                    .map { LatLng(it.latitude, it.longitude) }
            }.filter { it.size >= 2 }
        } else {
            val singlePath = pinEntries
                .sortedBy { it.timestamp }
                .map { LatLng(it.latitude, it.longitude) }
            if (singlePath.size >= 2) listOf(singlePath) else emptyList()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                // Google Maps blue dot — uses device GPS only, never uploaded anywhere.
                isMyLocationEnabled = hasLocationPermission && !useSimulatedLocation
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission
            )
        ) {
            // Simulated blue dot for emulator dev (client-only, not stored or shared).
            if (useSimulatedLocation && myLocation != null) {
                Circle(
                    center = myLocation,
                    radius = 30.0,
                    fillColor = Color(0x662196F3),
                    strokeColor = Color(0xFF2196F3),
                    strokeWidth = 4f
                )
            }

            pinEntries.forEach { entry ->
                val markerState = rememberMarkerState(
                    key = entry.id,
                    position = LatLng(entry.latitude, entry.longitude)
                )
                Marker(
                    state = markerState,
                    title = entry.dateRange.ifBlank { "Memory" },
                    snippet = entry.text,
                    onClick = {
                        onPinClick(entry)
                        true
                    }
                )
            }

            pathGroups.forEach { pathPoints ->
                Polyline(points = pathPoints)
            }
        }

        if (pinEntries.isEmpty()) {
            Text(
                text = "No memories mapped yet.\nAdd photos with location to a trip.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }
}
