package com.example.trove.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trove.Journal
import com.example.trove.JournalEntry
import com.example.trove.R
import com.example.trove.User
import com.example.trove.UserSaver
import com.example.trove.ui.screens.HomeScreen
import com.example.trove.ui.screens.JournalDetailScreen
import com.example.trove.ui.screens.JournalFormMode
import com.example.trove.ui.screens.JournalFormScreen
import com.example.trove.ui.screens.JournalViewsScreen
import com.example.trove.ui.screens.MapScreen
import com.example.trove.ui.screens.ProfileFormMode
import com.example.trove.ui.screens.ProfileFormScreen
import com.example.trove.ui.screens.ProfileScreen
import androidx.compose.runtime.rememberCoroutineScope
import com.example.trove.data.UserRepo
import kotlinx.coroutines.launch
import com.example.trove.ui.screens.ExpScreen

@Composable
fun UserNavigation(
    initialUser: User
) {
    var user by rememberSaveable(
        initialUser,
        stateSaver = UserSaver
    ) {
        mutableStateOf(initialUser)
    }

    val scope = rememberCoroutineScope()

    /*
    var journals by remember {
    mutableStateOf(
        listOf(
            Journal(
                id = "test-journal-1",
                ownerId = "brooke-uid",
                ownerName = "Brooke Cone",
                name = "San Francisco Trip",
                location = "San Francisco, CA",
                routeSummary = "Golden Gate → Fisherman's Wharf → Pier 39",
                entries = listOf(
                    JournalEntry(
                        id = "entry-1",
                        dateRange = "Jun 18",
                        text = "Golden Gate Bridge",
                        latitude = 37.8199,
                        longitude = -122.4783,
                        timestamp = 1L
                    ),
                    JournalEntry(
                        id = "entry-2",
                        dateRange = "Jun 18",
                        text = "Fisherman's Wharf",
                        latitude = 37.8080,
                        longitude = -122.4177,
                        timestamp = 2L
                    ),
                    JournalEntry(
                        id = "entry-3",
                        dateRange = "Jun 18",
                        text = "Pier 39",
                        latitude = 37.8087,
                        longitude = -122.4098,
                        timestamp = 3L
                    )
                )
            )
        )
    )
}

     */



    var journals by remember {
        mutableStateOf(listOf<Journal>())
    }

    var selectedJournal by remember {
        mutableStateOf<Journal?>(null)
    }


    var selectedFriend by remember {
        mutableStateOf<User?>(null)
    }

    var selectedFriendJournals by remember {
        mutableStateOf(listOf<Journal>())
    }

    // fake friends
    val alice = User(
        uid = "alice-uid",
        username = "alice",
        name = "Alice Smith",
        bio = "Collecting memories around the world.",
        countriesList = mutableListOf("France", "Italy"),
        friends = mutableListOf(),
        numJournals = 1,
        likes = 45
    )

    val aliceJournal = Journal(
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

    val friendUsers = listOf(alice)
    val friendJournals = listOf(aliceJournal)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hasRoute<Home>() == true,
                    onClick = {
                        navController.navigate(Home) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_explore), contentDescription = "Explore") },
                    label = { Text("Explore") },
                    selected = currentDestination?.hasRoute<Explore>() == true,
                    onClick = {
                        navController.navigate(Explore) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = "Add") },
                    label = { Text("Add") },
                    selected = currentDestination?.hasRoute<CreateJournal>() == true,
                    onClick = {
                        navController.navigate(CreateJournal) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_map), contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = currentDestination?.hasRoute<Map>() == true,
                    onClick = {
                        navController.navigate(Map) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentDestination?.hasRoute<Profile>() == true,
                    onClick = {
                        navController.navigate(Profile) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Home> {
                HomeScreen(
                    user = user,
                    friendsJournals = friendJournals,
                    onExplore = {
                        navController.navigate(Explore)
                    },
                    onJournals = {
                        navController.navigate(JournalViews)
                    },
                    onProfile = {
                        navController.navigate(Profile)
                    },
                    onSearch = {
                        navController.navigate(Explore)
                    },
                    onFriendClick = { friendUid ->
                        selectedFriend = friendUsers.find { friend ->
                            friend.uid == friendUid
                        }

                        selectedFriendJournals = friendJournals.filter { journal ->
                            journal.ownerId == friendUid
                        }

                        navController.navigate(FriendProfile)
                    },
                    onFriendJournalClick = { clickedJournal ->
                        selectedJournal = clickedJournal
                        navController.navigate(JournalDetail)
                    }
                )
            }

            composable<FriendProfile> {
                selectedFriend?.let { friend ->

                    val isFriend = friend.uid in user.friends

                    ProfileScreen(
                        user = friend,
                        isFriend = isFriend,
                        journals = selectedFriendJournals,
                        isCurrentUser = false,
                        onJournalClick = { clickedJournal ->
                            selectedJournal = clickedJournal
                            navController.navigate(JournalDetail)
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        onEditClick = {},

                        onAddFriendClick = {
                            val updatedFriends =
                                if (friend.uid in user.friends) {
                                    user.friends
                                        .filter { friendUid ->
                                            friendUid != friend.uid
                                        }
                                        .toMutableList()
                                } else {
                                    (
                                            user.friends + friend.uid
                                            ).toMutableList()
                                }

                            val updatedUser = user.copy(
                                friends = updatedFriends
                            )

                            user = updatedUser

                            scope.launch {
                                try {
                                    UserRepo.saveUser(updatedUser)
                                } catch (error: Exception) {
                                    println(
                                        error.message
                                            ?: "Could not update friend"
                                    )
                                }
                            }
                        },
                        onSettingsClick = {}
                    )
                }
            }

            composable<Explore> {
                composable<Explore> {
                    ExpScreen(
                        journals = journals + friendJournals,
                        onJournalClick = { clickedJournal ->
                            selectedJournal = clickedJournal
                            navController.navigate(JournalDetail)
                        }
                    )
                }
            }



            composable<CreateJournal> {
                JournalFormScreen(
                    initialJournal = Journal(),
                    mode = JournalFormMode.CREATE,
                    onSave = { createdJournal ->
                        val journalWithOwner = createdJournal.copy(
                            id = (journals.size + 1).toString(),
                            ownerId = user.uid,
                            ownerName = user.name,
                            ownerProfilePicture = user.profilePicture
                        )

                        journals = journals + journalWithOwner
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onAddPhotoClick = { /* Open photo picker */ }
                )
            }

            composable<EditJournal> {
                selectedJournal?.let { journal ->
                    JournalFormScreen(
                        initialJournal = journal,
                        mode = JournalFormMode.EDIT,
                        onSave = { updatedJournal ->

                            journals = journals.map { existingJournal ->
                                if (existingJournal.id == updatedJournal.id) {
                                    updatedJournal
                                } else {
                                    existingJournal
                                }
                            }

                            selectedJournal = updatedJournal
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        },
                        onAddPhotoClick = {
                            // connect photo picker later
                        }
                    )
                }
            }

            composable<Map> {
                val allEntries = journals.flatMap { it.entries }

                MapScreen(
                    entries = allEntries,
                    onPinClick = { entry ->
                        val journal = journals.find { j ->
                            j.entries.any { it.id == entry.id }
                        }
                        journal?.let {
                            selectedJournal = it
                            navController.navigate(JournalDetail)
                        }
                    }
                )
            }

            composable<Profile> {
                ProfileScreen(
                    user = user,
                    isFriend = false,
                    journals = journals,
                    isCurrentUser = true,
                    onJournalClick = { clickedJournal ->
                        selectedJournal = clickedJournal
                        navController.navigate(JournalDetail)
                    },
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(EditProfile) },
                    onAddFriendClick = {},
                    onSettingsClick = { /* fill in later */ }
                )
            }



            composable<JournalDetail> {
                selectedJournal?.let { journal ->
                    JournalDetailScreen(
                        journal = journal,
                        isOwner = journal.ownerId == user.uid,
                        onBack = {
                            navController.popBackStack()
                        },
                        onEditClick = {
                            navController.navigate(EditJournal)
                        }
                    )
                }
            }

            composable<EditProfile> {
                ProfileFormScreen(
                    initialProfile = user,
                    mode = ProfileFormMode.EDIT,
                    onSave = { updatedUser ->
                        user = updatedUser

                        scope.launch {
                            try {
                                UserRepo.saveUser(updatedUser)
                            } catch (error: Exception) {
                                println(
                                    error.message
                                        ?: "Could not save profile"
                                )
                            }
                        }

                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                )
            }

            composable<JournalViews> {
                JournalViewsScreen(
                    journals = journals,
                    onJournalClick = { clickedJournal ->
                        selectedJournal = clickedJournal
                        navController.navigate(JournalDetail)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
