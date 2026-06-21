package com.example.trove.ui.common

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import com.example.trove.Trip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.ui.theme.TroveTheme

@Composable
fun TripProfileCard(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ImagePlaceholder(
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = trip.name,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripProfileCardPreview() {
    TroveTheme {
        TripProfileCard(
            trip = Trip(
                id = "1",
                name = "Italy Trip",
                location = "Rome, Italy",
                routeSummary = "Rome → Florence",
                likes = 24
            ),
            onClick = {},
            modifier = Modifier.size(160.dp)
        )
    }
}