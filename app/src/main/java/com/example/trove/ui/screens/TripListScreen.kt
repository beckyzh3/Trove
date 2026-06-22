package com.example.trove.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.Trip
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.TripEntry
import com.example.trove.ui.common.TripPreviewCard
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.theme.TroveTheme

@Composable
fun TripListScreen(
    trips: List<Trip>,
    onTripClick: (Trip) -> Unit,
    onBack: () -> Unit
) {

    Scaffold (
        topBar = {
            TroveTopBar(
                title = "Trips",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(
                items = trips,
                key = { it.id }
            ) { trip ->

                TripPreviewCard(
                    trip = trip,
                    onClick = {
                        onTripClick(trip)
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TripListScreenPreview() {
    TroveTheme {
        TripListScreen(
            trips = listOf(
                Trip(
                    id = "1",
                    name = "Greece and Italy",
                    location = "Athens, Rome, Florence",
                    routeSummary = "Athens → Rome → Florence",
                    likes = 558
                ),
                Trip(
                    id = "2",
                    name = "Japan Adventure",
                    location = "Tokyo, Kyoto, Osaka",
                    routeSummary = "Tokyo → Kyoto → Osaka",
                    likes = 240
                ),
                Trip(
                    id = "3",
                    name = "Summer in Spain",
                    location = "Barcelona and Madrid",
                    routeSummary = "Barcelona → Madrid",
                    likes = 120
                )
            ),
            onTripClick = {},
            onBack = {}
        )
    }
}
