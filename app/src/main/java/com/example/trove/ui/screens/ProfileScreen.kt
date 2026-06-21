package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.R
import com.example.trove.Trip
import com.example.trove.User
import com.example.trove.ui.common.TripProfileCard
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.common.UserProfileCard

@Composable
fun ProfileScreen(
    user: User,
    isFriend: Boolean,
    trips: List<Trip>,
    isCurrentUser: Boolean,
    onTripClick: (Trip) -> Unit,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddFriendClick: () -> Unit
) {
    val tripCount = trips.size
    val totalLikes = trips.sumOf { it.likes }
    val countryCount = user.countriesList.size
    val friendCount = user.friends.size

    Scaffold(
        topBar = {
            TroveTopBar(
                title = "Profile",
                onBack = onBack,
                showBack = true,
                actions = {
                    if (isCurrentUser) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                UserProfileCard(
                    user = user,
                    isFriend = isFriend,
                    journalCount = tripCount,
                    countryCount = countryCount,
                    totalLikes = totalLikes,
                    friendCount = friendCount,
                    isCurrentUser = isCurrentUser,
                    onEditClick = onEditClick,
                    onAddFriendClick = onAddFriendClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = if (isCurrentUser) {
                        "My Trips"
                    } else {
                        "${user.name}'s Trips"
                    },
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (trips.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        if (isCurrentUser) {
                            "You haven't created any trips yet."
                        } else {
                            "${user.name} hasn't shared any trips yet."
                        }
                    )
                }
            } else {
                items(
                    items = trips,
                    key = { it.id }
                ) { trip ->
                    TripProfileCard(
                        trip = trip,
                        onClick = { onTripClick(trip) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        user = User(
            username = "beckzh3",
            name = "Becky Zheng",
            bio = "hiii",
            countriesList = mutableListOf("Italy", "Greece", "Spain"),
            friends = mutableListOf("Bob", "Kristen", "Sophie"),
            numJournals = 2,
            likes = 558
        ),
        isFriend = false,
        trips = listOf(
            Trip(
                id = "1",
                name = "Italy Trip",
                location = "Rome, Italy",
                routeSummary = "Rome → Florence",
                likes = 24
            ),
            Trip(
                id = "2",
                name = "Greece Trip",
                location = "Athens, Greece",
                routeSummary = "Athens → Santorini",
                likes = 18
            )
        ),
        isCurrentUser = true,
        onTripClick = {},
        onBack = {},
        onEditClick = {},
        onAddFriendClick = {},
        onSettingsClick = {}
    )
}
