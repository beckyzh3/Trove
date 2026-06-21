package com.example.trove.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.Trip

@Composable
fun TripPreviewCard(
    trip: Trip,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = trip.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = trip.location,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Text(
                text = trip.routeSummary,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${trip.likes} likes",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripPreviewCardPreview() {
    TripPreviewCard(
        trip = Trip(
            id = "1",
            name = "Greece and Italy",
            location = "Athens, Rome, Florence",
            routeSummary = "Athens → Rome → Florence",
            likes = 558
        ),
        onClick = {}
    )
}
