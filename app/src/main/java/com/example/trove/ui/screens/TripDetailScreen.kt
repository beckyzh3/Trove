package com.example.trove.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.example.trove.R
import com.example.trove.allPhotoUrls
import com.example.trove.isLikedBy
import com.example.trove.locationStops
import com.example.trove.mapStops
import com.example.trove.ui.common.ImagePlaceholder
import com.example.trove.ui.common.TripPhotoActionBar
import com.example.trove.ui.common.TripPhotoImage
import com.example.trove.ui.common.VoiceMemoPlayButton
import com.example.trove.ui.common.VoiceMemoRecorder
import com.example.trove.ui.theme.AutumnBrown
import com.example.trove.ui.theme.AutumnCream
import com.example.trove.ui.theme.AutumnOrange
import com.example.trove.ui.theme.TroveTheme
import com.example.trove.voiceMemos
import com.example.trove.data.PhotoEntryHelper
import com.example.trove.data.VoiceEntryHelper
import com.example.trove.data.rememberCameraCaptureLauncher
import kotlinx.coroutines.launch

private enum class TripTab(val label: String) {
    JOURNAL("Journal"),
    MAP("Map"),
    PHOTOS("Photos")
}

@Composable
fun TripDetailScreen(
    trip: Trip,
    currentUserId: String,
    isOwner: Boolean,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onPhotoTaken: (Trip) -> Unit = {},
    onVoiceMemoRecorded: (Trip) -> Unit = {},
    onLikeToggle: () -> Unit = {},
    likesEnabled: Boolean = true
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = TripTab.entries
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun addPhoto(uri: Uri) {
        scope.launch {
            val entry = PhotoEntryHelper.createPhotoEntry(context, uri)
            onPhotoTaken(PhotoEntryHelper.tripWithNewPhoto(trip, entry))
        }
    }

    val launchCamera = rememberCameraCaptureLauncher { uri -> addPhoto(uri) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { addPhoto(it) }
    }

    fun launchImport() {
        importLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        floatingActionButton = {
            if (isOwner) {
                FloatingActionButton(onClick = onEditClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit trip"
                    )
                }
            }
        },
        containerColor = AutumnCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TripHeroHeader(trip = trip, onBack = onBack)

            TripActionBar(
                likes = trip.likes,
                isLiked = trip.isLikedBy(currentUserId),
                likesEnabled = likesEnabled,
                onLikeClick = onLikeToggle
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AutumnCream,
                contentColor = AutumnBrown.copy(alpha = 0.5f),
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AutumnBrown,
                            height = 3.dp
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTabIndex == index
                    Tab(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = AutumnBrown,
                        unselectedContentColor = AutumnBrown.copy(alpha = 0.45f),
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (tab) {
                                    TripTab.JOURNAL -> Icon(
                                        painter = painterResource(R.drawable.ic_edit),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    TripTab.MAP -> Icon(
                                        painter = painterResource(R.drawable.ic_map),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    TripTab.PHOTOS -> Icon(
                                        painter = painterResource(R.drawable.ic_image_placeholder),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = tab.label,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }

            when (tabs[selectedTabIndex]) {
                TripTab.JOURNAL -> TripJournalTab(
                    trip = trip,
                    isOwner = isOwner,
                    onVoiceMemoRecorded = onVoiceMemoRecorded,
                    modifier = Modifier.weight(1f)
                )
                TripTab.MAP -> TripMapTab(trip = trip, modifier = Modifier.weight(1f))
                TripTab.PHOTOS -> TripPhotosTab(
                    trip = trip,
                    isOwner = isOwner,
                    onOpenCamera = launchCamera,
                    onImport = ::launchImport,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TripHeroHeader(trip: Trip, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        if (trip.coverPhotoUrl.isNotBlank()) {
            TripPhotoImage(
                url = trip.coverPhotoUrl,
                contentDescription = trip.name,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF3E2723), Color(0xFF6D4C41), Color(0x99000000))
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                    )
                )
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = AutumnOrange.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = trip.theme.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                if (trip.isPublic) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Public",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trip.name,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("📍 ${trip.location}", color = Color.White.copy(alpha = 0.9f))
            }
            if (trip.tripDates.isNotBlank()) {
                Text("📅 ${trip.tripDates}", color = Color.White.copy(alpha = 0.9f))
            }
            if (trip.distanceKm.isNotBlank()) {
                Text("🚶 ${trip.distanceKm}", color = Color.White.copy(alpha = 0.9f))
            } else if (trip.routeSummary.isNotBlank()) {
                Text("🚶 ${trip.routeSummary}", color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun TripActionBar(
    likes: Int,
    isLiked: Boolean,
    likesEnabled: Boolean,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = likesEnabled, onClick = onLikeClick)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLiked) "❤️" else "♥",
                style = MaterialTheme.typography.titleMedium,
                color = if (isLiked) AutumnOrange else AutumnBrown
            )
            Text(
                text = "$likes",
                style = MaterialTheme.typography.titleMedium,
                color = if (isLiked) AutumnOrange else AutumnBrown,
                fontWeight = if (isLiked) FontWeight.SemiBold else FontWeight.Normal
            )
        }
        Text(
            text = "🔖",
            style = MaterialTheme.typography.titleMedium
        )
    }
    HorizontalDivider()
}

@Composable
private fun TripJournalTab(
    trip: Trip,
    isOwner: Boolean,
    onVoiceMemoRecorded: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    val voiceEntries = trip.voiceMemos().filter { it.voiceMemoUrl.isNotBlank() }
    val locationEntries = trip.locationStops()
    val textEntries = trip.entries.filter {
        it.type == EntryType.TEXT && it.text.isNotBlank()
    }
    val hasJournalContent =
        voiceEntries.isNotEmpty() || locationEntries.isNotEmpty() || textEntries.isNotEmpty()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isOwner) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Record a voice memo",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AutumnBrown
                        )
                        VoiceMemoRecorder(
                            voiceMemoUrl = "",
                            voiceDuration = "",
                            onRecordingComplete = { url, duration ->
                                val entry = VoiceEntryHelper.createVoiceEntry(
                                    voiceMemoUrl = url,
                                    voiceDuration = duration
                                )
                                onVoiceMemoRecorded(
                                    VoiceEntryHelper.tripWithNewVoice(trip, entry)
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        if (voiceEntries.isNotEmpty()) {
            item {
                VoiceMemosCard(entries = voiceEntries)
            }
        }

        if (!hasJournalContent && !isOwner) {
            item {
                Text(
                    text = "No journal entries yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (!hasJournalContent && isOwner) {
            item {
                Text(
                    text = "Add text entries when editing, or record a voice memo above.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        items(locationEntries) { entry ->
            LocationEntryCard(entry = entry)
        }

        items(textEntries) { entry ->
            TextEntryCard(entry = entry)
        }
    }
}

@Composable
private fun VoiceMemosCard(entries: List<TripEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎙 Voice Memos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    color = AutumnCream,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${entries.size} recordings",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (entry.voiceMemoUrl.isNotBlank()) {
                        VoiceMemoPlayButton(voiceMemoUrl = entry.voiceMemoUrl)
                    } else {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = AutumnOrange.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("▶", color = AutumnOrange.copy(alpha = 0.4f))
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.text.ifBlank { "Voice memo" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = buildString {
                                if (entry.dateRange.isNotBlank()) append(entry.dateRange)
                                if (entry.voiceDuration.isNotBlank()) {
                                    if (isNotEmpty()) append(" · ")
                                    append(entry.voiceDuration)
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationEntryCard(entry: TripEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (entry.dateRange.isNotBlank()) {
                    "📍 ${entry.dateRange}"
                } else {
                    "📍 Location stop"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (entry.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${entry.latitude}, ${entry.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TextEntryCard(entry: TripEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "✈ ${entry.dateRange}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (entry.timeLabel.isNotBlank()) {
                Text(
                    text = entry.timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyLarge
            )
            if (entry.photoUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                TripPhotoImage(
                    url = entry.photoUrl,
                    contentDescription = entry.text.ifBlank { "Entry photo" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
private fun TripMapTab(trip: Trip, modifier: Modifier = Modifier) {
    val stops = trip.mapStops()
    if (stops.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No locations on this trip yet.\nEdit the trip to add map pins to entries.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        MapScreen(
            entries = stops,
            onPinClick = {},
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TripPhotosTab(
    trip: Trip,
    isOwner: Boolean,
    onOpenCamera: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photos = trip.allPhotoUrls()

    Column(modifier = modifier.fillMaxSize()) {
        if (isOwner) {
            TripPhotoActionBar(
                onOpenCamera = onOpenCamera,
                onImport = onImport
            )
        }

        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isOwner) {
                        "No photos yet.\nOpen Camera or Import to add one."
                    } else {
                        "No photos yet."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = AutumnBrown.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                itemsIndexed(photos, key = { _, url -> url }) { index, photoUrl ->
                    TripPhotoImage(
                        url = photoUrl,
                        contentDescription = "Trip photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(photoGridHeight(index))
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }
    }
}

private fun photoGridHeight(index: Int): Dp = when {
    index == 0 -> 280.dp
    index % 3 == 1 -> 130.dp
    else -> 165.dp
}

@Preview(showBackground = true)
@Composable
fun TripDetailScreenPreview() {
    TroveTheme {
        TripDetailScreen(
            trip = Trip(
                id = "1",
                name = "Lost in Tokyo's Backstreets",
                location = "Tokyo, Japan",
                tripDates = "May 12–19, 2024",
                distanceKm = "6.2 km",
                theme = "Vintage",
                likes = 247,
                entries = listOf(
                    TripEntry(
                        id = "v1",
                        type = EntryType.VOICE,
                        dateRange = "Day 1",
                        text = "First impressions of Shinjuku",
                        voiceDuration = "1:24"
                    ),
                    TripEntry(
                        id = "t1",
                        type = EntryType.TEXT,
                        dateRange = "Day 1 — May 12",
                        timeLabel = "8:34 PM",
                        text = "Landed at Narita feeling both exhausted and buzzing with excitement."
                    )
                )
            ),
            isOwner = true,
            currentUserId = "preview-user",
            onBack = {},
            onEditClick = {}
        )
    }
}
