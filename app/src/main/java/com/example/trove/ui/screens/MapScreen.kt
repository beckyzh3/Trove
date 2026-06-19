package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.JournalEntry
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(
    entries: List<JournalEntry>,
    onPinClick: (JournalEntry) -> Unit
) {
    val pinEntries = remember(entries) {
        entries.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    }

    if (pinEntries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No memories mapped yet.\nAdd photos with location to a journal.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val firstPin = LatLng(pinEntries.first().latitude, pinEntries.first().longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstPin, 12f)
    }

    val pathPoints = remember(pinEntries) {
        pinEntries
            .sortedBy { it.timestamp }
            .map { LatLng(it.latitude, it.longitude) }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
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

        if (pathPoints.size >= 2) {
            Polyline(points = pathPoints)
        }
    }
}