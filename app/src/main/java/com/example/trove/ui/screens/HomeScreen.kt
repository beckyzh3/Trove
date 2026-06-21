package com.example.trove.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.Journal
import com.example.trove.R
import com.example.trove.User
import com.example.trove.ui.common.ImagePlaceholder
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.common.UserProfilePicture
import com.example.trove.ui.theme.TroveTheme


// INCOMPLETE JUST A PLACEHOLDER
@Composable
fun HomeScreen(
    user: User,
    friendsJournals: List<Journal>,
    onProfile: () -> Unit,
    onSearch: () -> Unit,
    onExplore: () -> Unit,
    onJournals: () -> Unit,
    onNotifications: () -> Unit,
    onFriendClick: (String) -> Unit,
    onFriendJournalClick: (Journal) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val firstName = user.name.split(" ").firstOrNull() ?: user.name

    Scaffold(
        topBar = {
            TroveTopBar(
                title = "Welcome back, $firstName",
                actions = {

                    IconButton(onClick = onSearch) {
                        Icon(painter = painterResource(R.drawable.ic_settings), contentDescription = "Search")
                    }

                    IconButton(onClick = onProfile) {
                        UserProfilePicture(
                            profilePicture = user.profilePicture,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    IconButton(onClick = onNotifications){}
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onSearch() },
                placeholder = { Text("Search journals, places and people") },
                leadingIcon = { 
                    Icon(
                        painter = painterResource(R.drawable.ic_settings), 
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    ) 
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton("Journals", onClick = onJournals)
                FilterButton("Places")
                FilterButton("People")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Friends' Journals Section
            SectionHeader(title = "Friends' Journals")
            FriendsJournalsList(
                journals = friendsJournals,
                onFriendClick = onFriendClick,
                onJournalClick = onFriendJournalClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Explore Section
            SectionHeader(title = "Explore")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onExplore()
                    },
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ImagePlaceholder(modifier = Modifier.fillMaxSize())
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Text(
                        "Discover new destinations",
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trending Journals
            SectionHeader(title = "Trending Journals")
            TrendingJournalsList()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FilterButton(
    label: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun FriendsJournalsList(
    journals: List<Journal>,
    onFriendClick: (String) -> Unit,
    onJournalClick: (Journal) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(380.dp)
    ) {
        items(
            items = journals,
            key = { journal -> journal.id }
        ) { journal ->
            FriendJournalCard(
                journal = journal,
                onFriendClick = {
                    onFriendClick(journal.ownerId)
                },
                onJournalClick = {
                    onJournalClick(journal)
                }
            )
        }
    }
}

@Composable
fun FriendJournalCard(
    journal: Journal,
    onFriendClick: () -> Unit,
    onJournalClick: () -> Unit
) {
    Card(
        onClick = onJournalClick,
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Part
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onFriendClick()
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                UserProfilePicture(
                    profilePicture = journal.ownerProfilePicture,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = journal.ownerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = journal.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (journal.isPublic) "Public" else "Private",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = journal.theme,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ImagePlaceholder(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TrendingJournalsList() {
    val dummyJournals = listOf(
        Journal(name = "Summer in Italy", location = "Rome, Italy", likes = 120),
        Journal(name = "Japan Adventure", location = "Tokyo, Japan", likes = 85),
        Journal(name = "Swiss Alps Hike", location = "Zermatt, Switzerland", likes = 210)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(240.dp)
    ) {
        items(dummyJournals) { journal ->
            TrendingJournalCard(journal)
        }
    }
}

@Composable
fun TrendingJournalCard(journal: Journal) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            ImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = journal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = journal.location,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "❤️ ${journal.likes}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TroveTheme {
        HomeScreen(
            user = User(
                uid = "beck-uid",
                name = "Becky Zheng"),
            friendsJournals = listOf(
                Journal(
                    id = "alice-journal-1",
                    ownerId = "alice-uid",
                    ownerName = "Alice Smith",
                    name = "Paris Getaway",
                    location = "Paris, France",
                    routeSummary = "Montmartre → Louvre → Eiffel Tower",
                    isPublic = true,
                    theme = "Autumn Breeze",
                    likes = 45
                )
            ),
            onProfile = {},
            onSearch = {},
            onExplore = {},
            onJournals = {},
            onNotifications = {},
            onFriendClick = {},
            onFriendJournalClick = {}
        )
    }
}
