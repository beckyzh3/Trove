package com.example.trove.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.example.trove.TripSaver
import com.example.trove.R
import com.example.trove.location.LocationHelper
import com.example.trove.data.PhotoEntryHelper
import com.example.trove.data.rememberCameraCaptureLauncher
import com.example.trove.ui.common.TripPhotoActionBar
import com.example.trove.ui.common.TripPhotoImage
import com.example.trove.ui.common.VoiceMemoRecorder
import com.example.trove.ui.common.TroveTopBar
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.util.UUID

private sealed class PhotoPickTarget {
    data class Entry(val index: Int) : PhotoPickTarget()
    data object NewEntry : PhotoPickTarget()
}

enum class TripFormMode {
    CREATE,
    EDIT
}

@Composable
fun TripFormScreen(
    initialTrip: Trip,
    mode: TripFormMode,
    onSave: (Trip) -> Unit,
    onCancel: () -> Unit
) {

    var draftTrip by rememberSaveable(stateSaver = TripSaver) {
        mutableStateOf(initialTrip)
    }

    var entries by remember {
        mutableStateOf(
            if (initialTrip.entries.isEmpty()) {
                listOf(TripEntry(id = UUID.randomUUID().toString()))
            } else {
                initialTrip.entries
            }
        )
    }

    val submitAttempted = rememberSaveable { mutableStateOf(false) }
    val showDiscardDialog = rememberSaveable { mutableStateOf(false) }

    val hasUnsavedChanges =
        draftTrip != initialTrip ||
                entries != initialTrip.entries

    val isNameValid = draftTrip.name.isNotBlank()
    val isLocationValid = draftTrip.location.isNotBlank()

    val areEntriesValid = entries.all { entry ->
        when (entry.type) {
            EntryType.TEXT -> entry.dateRange.isNotBlank() && entry.text.isNotBlank()
            EntryType.VOICE -> entry.dateRange.isNotBlank() && entry.voiceMemoUrl.isNotBlank()
            EntryType.PHOTO -> entry.dateRange.isNotBlank() && entry.photoUrl.isNotBlank()
            EntryType.LOCATION -> entry.latitude != 0.0 && entry.longitude != 0.0
        }
    }

    val isFormValid =
        isNameValid &&
                isLocationValid &&
                entries.isNotEmpty() &&
                areEntriesValid

    val screenTitle = when (mode) {
        TripFormMode.CREATE -> "Create Trip"
        TripFormMode.EDIT -> "Edit Trip"
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showDiscardDialog.value = true
    }

    if (showDiscardDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDiscardDialog.value = false
            },
            title = {
                Text("Discard changes?")
            },
            text = {
                Text("You haven't saved your trip.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog.value = false
                        onCancel()
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog.value = false
                    }
                ) {
                    Text("Keep editing")
                }
            }
        )
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingPhotoTarget by remember { mutableStateOf<PhotoPickTarget?>(null) }

    fun applyPickedPhoto(picked: Uri, target: PhotoPickTarget) {
        scope.launch {
            when (target) {
                is PhotoPickTarget.Entry -> {
                    val current = entries[target.index]
                    val photoEntry = PhotoEntryHelper.createPhotoEntry(
                        context = context,
                        uri = picked,
                        dateRange = current.dateRange.ifBlank { "New photo" }
                    )
                    entries = entries.toMutableList().also {
                        it[target.index] = current.copy(
                            photoUrl = photoEntry.photoUrl,
                            latitude = photoEntry.latitude.takeIf { lat -> lat != 0.0 }
                                ?: current.latitude,
                            longitude = photoEntry.longitude.takeIf { lng -> lng != 0.0 }
                                ?: current.longitude,
                            timestamp = if (current.timestamp == 0L) {
                                photoEntry.timestamp
                            } else {
                                current.timestamp
                            }
                        )
                    }
                }
                PhotoPickTarget.NewEntry -> {
                    entries = entries + PhotoEntryHelper.createPhotoEntry(
                        context = context,
                        uri = picked,
                        dateRange = "New photo"
                    )
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { picked ->
            pendingPhotoTarget?.let { target ->
                applyPickedPhoto(picked, target)
            }
        }
        pendingPhotoTarget = null
    }

    fun launchPhotoPicker(target: PhotoPickTarget) {
        pendingPhotoTarget = target
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val launchCamera = rememberCameraCaptureLauncher { uri ->
        pendingPhotoTarget?.let { target ->
            applyPickedPhoto(uri, target)
        }
        pendingPhotoTarget = null
    }

    fun launchCameraFor(target: PhotoPickTarget) {
        pendingPhotoTarget = target
        launchCamera()
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasLocationPermission =
            results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    fun applyCurrentLocation(index: Int) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }
        scope.launch {
            val coords = LocationHelper.getCurrentLatLng(context)
                ?: if (LocationHelper.isEmulator()) LocationHelper.EMULATOR_SF else null
            coords?.let { latLng ->
                entries = entries.toMutableList().also {
                    it[index] = entries[index].copy(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TroveTopBar(
                title = screenTitle,
                showBack = true,
                onBack = {
                    if (hasUnsavedChanges) {
                        showDiscardDialog.value = true
                    } else {
                        onCancel()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    submitAttempted.value = true

                    if (isFormValid) {
                        onSave(
                            draftTrip.copy(
                                entries = entries
                            )
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = when (mode) {
                        TripFormMode.CREATE -> "Create trip"
                        TripFormMode.EDIT -> "Save trip"
                    }
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = draftTrip.name,
                onValueChange = {
                    draftTrip = draftTrip.copy(name = it)
                },
                label = {
                    Text("Trip name")
                },
                isError = submitAttempted.value && !isNameValid,
                supportingText = {
                    if (submitAttempted.value && !isNameValid) {
                        Text("Trip name cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftTrip.location,
                onValueChange = {
                    draftTrip = draftTrip.copy(location = it)
                },
                label = {
                    Text("Location")
                },
                isError = submitAttempted.value && !isLocationValid,
                supportingText = {
                    if (submitAttempted.value && !isLocationValid) {
                        Text("Location cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftTrip.tripDates,
                onValueChange = {
                    draftTrip = draftTrip.copy(tripDates = it)
                },
                label = { Text("Trip dates") },
                placeholder = { Text("May 12–19, 2024") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftTrip.distanceKm,
                onValueChange = {
                    draftTrip = draftTrip.copy(distanceKm = it)
                },
                label = { Text("Distance (optional)") },
                placeholder = { Text("6.2 km") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Trip stops & entries")

            entries.forEachIndexed { index, entry ->

                TripEntryEditor(
                    entry = entry,
                    showErrors = submitAttempted.value,
                    onEntryChange = { updatedEntry ->
                        entries = entries.toMutableList().also {
                            it[index] = updatedEntry
                        }
                    },
                    onDeleteClick = {
                        entries = entries.toMutableList().also {
                            it.removeAt(index)
                        }
                    },
                    onUseCurrentLocation = { applyCurrentLocation(index) },
                    onPickPhoto = { launchPhotoPicker(PhotoPickTarget.Entry(index)) },
                    onTakePhoto = { launchCameraFor(PhotoPickTarget.Entry(index)) }
                )
            }

            Button(
                onClick = {
                    entries = entries + TripEntry(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Entry")
            }

            OutlinedTextField(
                value = draftTrip.routeSummary,
                onValueChange = {
                    draftTrip =
                        draftTrip.copy(routeSummary = it)
                },
                label = {
                    Text("Route summary")
                },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    entries = entries + TripEntry(
                        id = UUID.randomUUID().toString(),
                        type = EntryType.LOCATION,
                        timestamp = System.currentTimeMillis()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Location Stop")
            }

            TripPhotoActionBar(
                onOpenCamera = { launchCameraFor(PhotoPickTarget.NewEntry) },
                onImport = { launchPhotoPicker(PhotoPickTarget.NewEntry) }
            )

            Button(
                onClick = {
                    entries = entries + TripEntry(
                        id = UUID.randomUUID().toString(),
                        type = EntryType.VOICE,
                        dateRange = "Voice memo",
                        timestamp = System.currentTimeMillis()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Voice Memo")
            }
        }
    }
}

@Composable
fun TripEntryEditor(
    entry: TripEntry,
    showErrors: Boolean,
    onEntryChange: (TripEntry) -> Unit,
    onDeleteClick: () -> Unit,
    onUseCurrentLocation: () -> Unit = {},
    onPickPhoto: () -> Unit = {},
    onTakePhoto: () -> Unit = {}
) {
    val isDateValid = entry.type == EntryType.LOCATION || entry.dateRange.isNotBlank()
    val hasPin = entry.latitude != 0.0 && entry.longitude != 0.0
    val isContentValid = when (entry.type) {
        EntryType.TEXT -> entry.text.isNotBlank()
        EntryType.VOICE -> entry.voiceMemoUrl.isNotBlank()
        EntryType.PHOTO -> entry.photoUrl.isNotBlank()
        EntryType.LOCATION -> hasPin
    }
    val isLocationOnlyEntry = entry.type == EntryType.LOCATION
    val supportsOptionalLocation =
        entry.type == EntryType.TEXT ||
            entry.type == EntryType.VOICE ||
            entry.type == EntryType.PHOTO
    var includeLocation by remember(entry.id, entry.latitude, entry.longitude) {
        mutableStateOf(hasPin)
    }
    val showLocationEditor = isLocationOnlyEntry || (supportsOptionalLocation && includeLocation)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Entry type", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EntryType.entries.forEach { type ->
                    FilterChip(
                        selected = entry.type == type,
                        onClick = { onEntryChange(entry.copy(type = type)) },
                        label = {
                            Text(
                                when (type) {
                                    EntryType.TEXT -> "Text"
                                    EntryType.VOICE -> "Voice"
                                    EntryType.PHOTO -> "Photo"
                                    EntryType.LOCATION -> "Location"
                                }
                            )
                        }
                    )
                }
            }

            OutlinedTextField(
                value = entry.dateRange,
                onValueChange = { onEntryChange(entry.copy(dateRange = it)) },
                label = {
                    Text(
                        if (isLocationOnlyEntry) {
                            "Date / day label (optional)"
                        } else {
                            "Date / day label"
                        }
                    )
                },
                isError = showErrors && !isDateValid,
                modifier = Modifier.fillMaxWidth()
            )

            if (entry.type == EntryType.TEXT) {
                OutlinedTextField(
                    value = entry.timeLabel,
                    onValueChange = { onEntryChange(entry.copy(timeLabel = it)) },
                    label = { Text("Time (optional)") },
                    placeholder = { Text("8:34 PM") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (entry.type == EntryType.VOICE) {
                VoiceMemoRecorder(
                    voiceMemoUrl = entry.voiceMemoUrl,
                    voiceDuration = entry.voiceDuration,
                    onRecordingComplete = { url, duration ->
                        onEntryChange(
                            entry.copy(
                                voiceMemoUrl = url,
                                voiceDuration = duration
                            )
                        )
                    }
                )
            }

            if (entry.type == EntryType.PHOTO) {
                if (entry.photoUrl.isNotBlank()) {
                    TripPhotoImage(
                        url = entry.photoUrl,
                        contentDescription = entry.text.ifBlank { "Trip photo" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                TripPhotoActionBar(
                    onOpenCamera = onTakePhoto,
                    onImport = onPickPhoto,
                    compact = true
                )
            }

            if (isLocationOnlyEntry) {
                OutlinedTextField(
                    value = entry.text,
                    onValueChange = { onEntryChange(entry.copy(text = it)) },
                    label = { Text("Stop name (optional)") },
                    placeholder = { Text("e.g. Shinjuku Station") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = entry.text,
                    onValueChange = { onEntryChange(entry.copy(text = it)) },
                    label = {
                        Text(
                            when (entry.type) {
                                EntryType.TEXT -> "Entry text"
                                EntryType.VOICE -> "Voice memo title"
                                EntryType.PHOTO -> "Photo caption"
                                EntryType.LOCATION -> "Stop name (optional)"
                            }
                        )
                    },
                    minLines = if (entry.type == EntryType.TEXT) 4 else 1,
                    isError = showErrors && !isContentValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (supportsOptionalLocation) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add map pin (optional)", style = MaterialTheme.typography.titleSmall)
                    Switch(
                        checked = includeLocation,
                        onCheckedChange = { enabled ->
                            includeLocation = enabled
                            if (!enabled) {
                                onEntryChange(entry.copy(latitude = 0.0, longitude = 0.0))
                            }
                        }
                    )
                }
            }

            if (isLocationOnlyEntry) {
                Text("Location", style = MaterialTheme.typography.titleSmall)
            }

            if (showLocationEditor) {
                EntryLocationFields(
                    entry = entry,
                    showErrors = showErrors && isLocationOnlyEntry,
                    onEntryChange = onEntryChange,
                    onUseCurrentLocation = onUseCurrentLocation
                )
            }

            TextButton(onClick = onDeleteClick) {
                Text("Delete entry")
            }
        }
    }
}

@Composable
private fun EntryLocationFields(
    entry: TripEntry,
    showErrors: Boolean,
    onEntryChange: (TripEntry) -> Unit,
    onUseCurrentLocation: () -> Unit
) {
    val hasPin = entry.latitude != 0.0 && entry.longitude != 0.0

    Text(
        text = if (hasPin) {
            "Pin at ${entry.latitude}, ${entry.longitude}"
        } else {
            "Set a pin to mark this stop on the Map tab"
        },
        style = MaterialTheme.typography.bodySmall,
        color = if (showErrors && !hasPin) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = if (entry.latitude == 0.0) "" else entry.latitude.toString(),
            onValueChange = { value ->
                onEntryChange(entry.copy(latitude = value.toDoubleOrNull() ?: 0.0))
            },
            label = { Text("Latitude") },
            isError = showErrors && !hasPin,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = if (entry.longitude == 0.0) "" else entry.longitude.toString(),
            onValueChange = { value ->
                onEntryChange(entry.copy(longitude = value.toDoubleOrNull() ?: 0.0))
            },
            label = { Text("Longitude") },
            isError = showErrors && !hasPin,
            modifier = Modifier.weight(1f)
        )
    }

    Button(onClick = onUseCurrentLocation, modifier = Modifier.fillMaxWidth()) {
        Text("Use my current location")
    }

    EntryPinMap(
        entryId = entry.id.ifBlank { "new-entry" },
        latitude = entry.latitude,
        longitude = entry.longitude,
        onLocationChange = { lat, lng ->
            onEntryChange(
                entry.copy(
                    latitude = lat,
                    longitude = lng,
                    timestamp = if (entry.timestamp == 0L) {
                        System.currentTimeMillis()
                    } else {
                        entry.timestamp
                    }
                )
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateTripFormPreview() {
    TripFormScreen(
        initialTrip = Trip(),
        mode = TripFormMode.CREATE,
        onSave = {},
        onCancel = {}
    )
}


@Preview(showBackground = true)
@Composable
fun EditTripFormPreview() {
    TripFormScreen(
        initialTrip = Trip(
            name = "Greece and Italy",
            location = "Athens, Rome, Florence",
            entries = listOf(
                TripEntry(
                    dateRange = "May 20–22",
                    text = "Explored Athens and visited the Acropolis."
                ),
                TripEntry(
                    dateRange = "May 23–25",
                    text = "Traveled to Rome and visited the Colosseum."
                )
            ),
            routeSummary = "Athens → Rome → Florence"
        ),
        mode = TripFormMode.EDIT,
        onSave = {},
        onCancel = {}
    )
}
