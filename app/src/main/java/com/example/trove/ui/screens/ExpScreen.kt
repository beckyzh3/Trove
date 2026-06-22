package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.Trip
import com.example.trove.ui.common.TripPreviewCard
import com.example.trove.ui.common.TroveTopBar

@Composable
fun ExpScreen(
    trips: List<Trip>,
    onTripClick: (Trip) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Trips") }


    val publicJournals = journals.filter { it.isPublic }

    val searchResults = journals.filter { journal ->
        val query = searchQuery.trim()

        when (selectedFilter) {
            "Journals" -> {
                query.isBlank() || journal.name.contains(query, ignoreCase = true)
            }

            "Places" -> {
                query.isBlank() || journal.location.contains(query, ignoreCase = true)
            }

            "People" -> {
                query.isBlank() || journal.ownerName.contains(query, ignoreCase = true)
            }

            else -> true
        }
    }

    val trendingTrips = publicTrips.sortedByDescending { it.likes }

    Scaffold(
        topBar = {
            TroveTopBar(title = "Explore")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search trips, places and people") },
                    shape = MaterialTheme.shapes.extraLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterButton(label = "Trips", onClick = { selectedFilter = "Trips" })
                    FilterButton(label = "Places", onClick = { selectedFilter = "Places" })
                    FilterButton(label = "People", onClick = { selectedFilter = "People" })
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "$selectedFilter Results")
            }

            items(items = searchResults, key = { it.id }) { trip ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    TripPreviewCard(
                        trip = trip,
                        onClick = { onTripClick(trip) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Trending Trips")

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(items = trendingTrips, key = { it.id }) { trip ->
                        TrendingTripCard(trip = trip)
                    }
                }
            }
        }
    }
}
