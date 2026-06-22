package com.example.trove.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trove.R
import com.example.trove.Trip
import com.example.trove.User
import com.example.trove.UserSaver
import com.example.trove.data.BundledExploreTrips
import com.example.trove.data.DemoTripSeeder
import com.example.trove.data.TripRepository
import com.example.trove.data.UserRepo
import com.example.trove.ui.screens.ExpScreen
import com.example.trove.ui.screens.HomeScreen
import com.example.trove.ui.screens.MapScreen
import com.example.trove.ui.screens.ProfileFormMode
import com.example.trove.ui.screens.ProfileFormScreen
import com.example.trove.ui.screens.ProfileScreen
import com.example.trove.ui.screens.TripDetailScreen
import com.example.trove.ui.screens.TripFormMode
import com.example.trove.ui.screens.TripFormScreen
import com.example.trove.ui.screens.TripListScreen
import kotlinx.coroutines.launch
import com.example.trove.ui.screens.NotificationsScreen

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

    val repository = remember { TripRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var trips by remember {
        mutableStateOf(listOf<Trip>())
    }

    var exploreTrips by remember {
        mutableStateOf(listOf<Trip>())
    }

    var selectedTrip by remember {
        mutableStateOf<Trip?>(null)
    }

    suspend fun refreshTrips() {
        if (user.uid.isBlank()) return
        val fromFirestore = runCatching {
            repository.getTripsWithEntries(user.uid)
        }.getOrDefault(emptyList())
        trips = BundledExploreTrips.applyAll(
            context,
            DemoTripSeeder.mergeLocalDemoTrip(fromFirestore, user.uid, user.name)
        )
    }

    suspend fun refreshExploreTrips() {
        val fromFirestore = runCatching {
            repository.getPublicTrips()
        }.getOrDefault(emptyList())
        exploreTrips = BundledExploreTrips.applyAll(
            context,
            BundledExploreTrips.mergeWithFirestore(fromFirestore)
        )
    }

    fun applyTripUpdate(updated: Trip) {
        val trip = BundledExploreTrips.applyTrip(context, updated)
        trips = trips.map { if (it.id == trip.id) trip else it }
        exploreTrips = exploreTrips.map { if (it.id == trip.id) trip else it }
        if (selectedTrip?.id == trip.id) {
            selectedTrip = trip
        }
    }

    LaunchedEffect(user.uid) {
        if (user.uid.isNotBlank()) {
            runCatching {
                DemoTripSeeder.ensureDemoTrips(
                    repository = repository,
                    ownerId = user.uid,
                    ownerName = user.name
                )
            }
        }
        refreshTrips()
        refreshExploreTrips()
    }

    var selectedFriend by remember {
        mutableStateOf<User?>(null)
    }

    var selectedFriendTrips by remember {
        mutableStateOf(listOf<Trip>())
    }

    val friendUsers = listOf(BundledExploreTrips.demoFriendUser)
    val friendTrips = exploreTrips.filter { it.ownerId != user.uid }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var isSavingTrip by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    suspend fun saveTripAndRefresh(trip: Trip) {
        isSavingTrip = true
        try {
            repository.saveTripWithLocalMedia(context, trip)
            refreshTrips()
        } finally {
            isSavingTrip = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_home), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hasRoute<Home>() == true,
                    onClick = {
                        navController.navigate(Home) {
                            popUpTo(
                                navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_explore), contentDescription = "Explore") },
                    label = { Text("Explore") },
                    selected = currentDestination?.hasRoute<Explore>() == true,
                    onClick = {
                        navController.navigate(Explore) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = "Add") },
                    label = { Text("Add") },
                    selected = currentDestination?.hasRoute<CreateTrip>() == true,
                    onClick = {
                        navController.navigate(CreateTrip) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_map), contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = currentDestination?.hasRoute<Map>() == true,
                    onClick = {
                        navController.navigate(Map) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentDestination?.hasRoute<Profile>() == true,
                    onClick = {
                        navController.navigate(Profile) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                            restoreState = false
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
                    friendsTrips = friendTrips,
                    trendingTrips = exploreTrips,
                    onExplore = {
                        navController.navigate(Explore)
                    },
                    onTrips = {
                        navController.navigate(TripList)
                    },
                    onProfile = {
                        navController.navigate(Profile)
                    },
                    onSearch = {
                        navController.navigate(Explore)
                    },
                    onNotifications = {
                        navController.navigate(Notifications)
                    },
                    onFriendClick = { friendUid ->
                        selectedFriend = friendUsers.find { friend ->
                            friend.uid == friendUid
                        }

                        selectedFriendTrips = friendTrips.filter { trip ->
                            trip.ownerId == friendUid
                        }

                        navController.navigate(FriendProfile)
                    },
                    onFriendTripClick = { clickedTrip ->
                        selectedTrip = clickedTrip
                        navController.navigate(TripDetail)
                    }
                )
            }

                composable<FriendProfile> {
                    selectedFriend?.let { friend ->
                        val isFriend = friend.uid in user.friends

                        ProfileScreen(
                            user = friend,
                            isFriend = isFriend,
                            trips = selectedFriendTrips,
                            isCurrentUser = false,
                            onTripClick = { clickedTrip ->
                                selectedTrip = clickedTrip
                                navController.navigate(TripDetail)
                            },
                            onBack = { navController.popBackStack() },
                            onEditClick = {},
                            onAddFriendClick = {
                                val updatedFriends =
                                    if (friend.uid in user.friends) {
                                        user.friends.filter { friendUid -> friendUid != friend.uid }.toMutableList()
                                    } else {
                                        (user.friends + friend.uid).toMutableList()
                                    }

                                val updatedUser = user.copy(friends = updatedFriends)
                                user = updatedUser

                                scope.launch {
                                    try {
                                        UserRepo.saveUser(updatedUser)
                                    } catch (error: Exception) {
                                        snackbarHostState.showSnackbar(
                                            error.message ?: "Could not update friend"
                                        )
                                    }
                                }
                            },
                            onSettingsClick = {}
                        )
                    }
                }

                composable<Explore> {
                    ExpScreen(
                        trips = exploreTrips,
                        onTripClick = { clickedTrip ->
                            selectedTrip = clickedTrip
                            navController.navigate(TripDetail)
                        }
                    )
                }

                composable<CreateTrip> {
                    TripFormScreen(
                        initialTrip = Trip(),
                        mode = TripFormMode.CREATE,
                        onSave = { createdTrip ->
                            val tripWithOwner = createdTrip.copy(
                                ownerId = user.uid,
                                ownerName = user.name,
                                ownerProfilePicture = user.profilePicture
                            )
                            scope.launch {
                                try {
                                    saveTripAndRefresh(tripWithOwner)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(
                                        e.message ?: "Could not save trip."
                                    )
                                }
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable<EditTrip> {
                    selectedTrip?.let { trip ->
                        TripFormScreen(
                            initialTrip = trip,
                            mode = TripFormMode.EDIT,
                            onSave = { updatedTrip ->
                                scope.launch {
                                    try {
                                        saveTripAndRefresh(updatedTrip)
                                        val refreshed = BundledExploreTrips.applyAll(
                                            context,
                                            DemoTripSeeder.mergeLocalDemoTrip(
                                                repository.getTripsWithEntries(user.uid),
                                                user.uid,
                                                user.name
                                            )
                                        )
                                        trips = refreshed
                                        selectedTrip = refreshed.find { it.id == updatedTrip.id }
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            e.message ?: "Could not save trip."
                                        )
                                    }
                                }
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }

                composable<Map> {
                    val allEntries = trips.flatMap { it.entries }
                    val tripPaths = trips.map { trip ->
                        trip.entries
                            .filter { it.latitude != 0.0 && it.longitude != 0.0 }
                            .sortedBy { it.timestamp }
                    }

                    MapScreen(
                        entries = allEntries,
                        tripPaths = tripPaths,
                        onPinClick = { entry ->
                            val trip = trips.find { t ->
                                t.entries.any { it.id == entry.id }
                            }
                            trip?.let {
                                selectedTrip = it
                                navController.navigate(TripDetail)
                            }
                        }
                    )
                }

                composable<Profile> {
                    ProfileScreen(
                        user = user,
                        isFriend = false,
                        trips = trips,
                        isCurrentUser = true,
                        onTripClick = { clickedTrip ->
                            selectedTrip = clickedTrip
                            navController.navigate(TripDetail)
                        },
                        onBack = { navController.popBackStack() },
                        onEditClick = { navController.navigate(EditProfile) },
                        onAddFriendClick = {},
                        onSettingsClick = { /* fill in later */ }
                    )
                }

                composable<TripDetail> {
                    selectedTrip?.let { trip ->
                        val onTripUpdated: (Trip) -> Unit = { updatedTrip ->
                            scope.launch {
                                try {
                                    saveTripAndRefresh(updatedTrip)
                                    selectedTrip = trips.find { it.id == updatedTrip.id }
                                        ?: updatedTrip
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(
                                        e.message
                                            ?: "Could not save trip."
                                    )
                                }
                            }
                        }

                        TripDetailScreen(
                            trip = trip,
                            currentUserId = user.uid,
                            isOwner = trip.ownerId == user.uid,
                            onBack = { navController.popBackStack() },
                            onEditClick = { navController.navigate(EditTrip) },
                            onPhotoTaken = onTripUpdated,
                            onVoiceMemoRecorded = onTripUpdated,
                            onLikeToggle = {
                                scope.launch {
                                    try {
                                        val updated = repository.toggleLike(trip.id, user.uid)
                                        applyTripUpdate(updated)
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            e.message ?: "Could not update like"
                                        )
                                    }
                                }
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
                                    snackbarHostState.showSnackbar(
                                        error.message ?: "Could not save profile"
                                    )
                                }
                            }
                            navController.popBackStack()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }

            composable<Notifications> {

                NotificationsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<TripList> {
                TripListScreen(
                    trips = trips,
                    onTripClick = { clickedTrip ->
                        selectedTrip = clickedTrip
                        navController.navigate(TripDetail)
                    },
                    onBack = { navController.popBackStack() }
                )
            }



        }

            if (isSavingTrip) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Saving trip…",
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

