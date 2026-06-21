package com.example.trove.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.EntryType
import com.example.trove.Trip
import com.example.trove.TripEntry
import com.example.trove.R
import com.example.trove.allPhotoUrls
import com.example.trove.locationStops
import com.example.trove.mapStops
import com.example.trove.ui.common.ImagePlaceholder
import com.example.trove.ui.common.TripPhotoImage
import com.example.trove.ui.theme.AutumnBrown
import com.example.trove.ui.theme.AutumnCream
import com.example.trove.ui.theme.AutumnOrange
import com.example.trove.ui.theme.TroveTheme
import com.example.trove.voiceMemos

private enum class TripTab(val label: String) {
    JOURNAL("Journal"),
    MAP("Map"),
    PHOTOS("Photos")
}

@Composable
fun TripDetailScreen(
    trip: Trip,
    isOwner: Boolean,
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = TripTab.entries

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

            TripActionBar(likes = trip.likes)

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AutumnCream,
                contentColor = AutumnBrown
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
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
                                Text(tab.label)
                            }
                        }
                    )
                }
            }

            when (tabs[selectedTabIndex]) {
                TripTab.JOURNAL -> TripJournalTab(trip = trip, modifier = Modifier.weight(1f))
                TripTab.MAP -> TripMapTab(trip = trip, modifier = Modifier.weight(1f))
                TripTab.PHOTOS -> TripPhotosTab(trip = trip, modifier = Modifier.weight(1f))
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
private fun TripActionBar(likes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "♥ $likes",
            style = MaterialTheme.typography.titleMedium,
            color = AutumnBrown
        )
        Text(
            text = "🔖",
            style = MaterialTheme.typography.titleMedium
        )
    }
    HorizontalDivider()
}

@Composable
private fun TripJournalTab(trip: Trip, modifier: Modifier = Modifier) {
    val voiceEntries = trip.voiceMemos()
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
        if (voiceEntries.isNotEmpty()) {
            item {
                VoiceMemosCard(entries = voiceEntries)
            }
        }

        if (!hasJournalContent) {
            item {
                Text(
                    text = "No journal entries yet. Edit this trip to add stops, text, or voice memos.",
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
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = AutumnOrange.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("▶", color = AutumnOrange)
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
private fun TripPhotosTab(trip: Trip, modifier: Modifier = Modifier) {
    val photos = trip.allPhotoUrls()

    if (photos.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No photos yet.\nAdd photo entries when editing this trip.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(photos, key = { it }) { photoUrl ->
                TripPhotoImage(
                    url = photoUrl,
                    contentDescription = "Trip photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
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
            onBack = {},
            onEditClick = {}
        )
    }
}
