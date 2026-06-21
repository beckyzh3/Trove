package com.example.trove.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.location.LocationHelper
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlin.math.abs

private const val DOUBLE_TAP_WINDOW_MS = 400L

@Composable
fun EntryPinMap(
    entryId: String,
    latitude: Double,
    longitude: Double,
    onLocationChange: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val startPosition = remember(entryId) {
        if (latitude != 0.0 && longitude != 0.0) {
            LatLng(latitude, longitude)
        } else {
            LocationHelper.EMULATOR_SF
        }
    }

    val markerState = rememberMarkerState(key = entryId, position = startPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, 14f)
    }

    var lastTapTime by remember { mutableLongStateOf(0L) }
    var pendingDoubleTap by remember { mutableStateOf(false) }

    fun placePin(latLng: LatLng) {
        markerState.position = latLng
        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
        onLocationChange(latLng.latitude, latLng.longitude)
    }

    // Sync text-field / GPS updates into the marker.
    LaunchedEffect(latitude, longitude) {
        if (latitude == 0.0 && longitude == 0.0) return@LaunchedEffect
        val target = LatLng(latitude, longitude)
        if (!markerState.position.isNear(target)) {
            markerState.position = target
        }
    }

    // Sync marker drag back into the entry fields.
    LaunchedEffect(markerState) {
        snapshotFlow { markerState.position }
            .collect { position ->
                if (!position.isNear(latitude, longitude)) {
                    onLocationChange(position.latitude, position.longitude)
                }
            }
    }

    Text(
        text = "Double-tap the map to place the pin. Long-press also works on emulator. Drag to fine-tune.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    GoogleMap(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            scrollGesturesEnabled = true
        ),
        onMapClick = { latLng ->
            val now = System.currentTimeMillis()
            if (pendingDoubleTap && now - lastTapTime <= DOUBLE_TAP_WINDOW_MS) {
                placePin(latLng)
                pendingDoubleTap = false
                lastTapTime = 0L
            } else {
                lastTapTime = now
                pendingDoubleTap = true
            }
        },
        onMapLongClick = { latLng ->
            pendingDoubleTap = false
            lastTapTime = 0L
            placePin(latLng)
        }
    ) {
        Marker(
            state = markerState,
            draggable = true,
            title = "Drag to move pin"
        )
    }
}

private fun LatLng.isNear(other: LatLng, epsilon: Double = 0.00001): Boolean {
    return abs(latitude - other.latitude) < epsilon &&
        abs(longitude - other.longitude) < epsilon
}

private fun LatLng.isNear(lat: Double, lng: Double, epsilon: Double = 0.00001): Boolean {
    return abs(latitude - lat) < epsilon && abs(longitude - lng) < epsilon
}
