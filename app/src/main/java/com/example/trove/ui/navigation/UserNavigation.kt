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
import com.example.trove.R
import com.example.trove.User
import com.example.trove.UserSaver
import com.example.trove.ui.screens.HomeScreen
import com.example.trove.ui.screens.JournalFormMode
import com.example.trove.ui.screens.JournalFormScreen
import com.example.trove.ui.screens.ProfileFormMode
import com.example.trove.ui.screens.ProfileFormScreen
import com.example.trove.ui.screens.ProfileScreen

@Composable
fun UserNavigation() {
    var user by rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf(User(
            username = "beckzh3",
            name = "Becky Zheng",
            bio = "Traveling the world one pin at a time!",
            countriesList = mutableListOf("Italy", "Greece", "Spain", "Japan"),
            friends = mutableListOf("Alice", "Bob", "Charlie"),
            followers = 1250,
            numJournals = 4,
            likes = 842
        ))
    }

    var journal by remember {
        mutableStateOf(Journal())
    }


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
                    onProfile = {
                        navController.navigate(Profile)
                    }
                )
            }

            composable<Explore> {
                Surface {
                    Text("Explore Screen Placeholder")
                }
            }

            composable<CreateJournal> {
                JournalFormScreen(
                    initialJournal = Journal(),
                    mode = JournalFormMode.CREATE,
                    onSave = {
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onAddPhotoClick = { /* Open photo picker */ }
                )
            }

            composable<Map> {
                Surface {
                    Text("Map Screen Placeholder")
                }
            }

            composable<Profile> {
                ProfileScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(EditProfile) },
                    onSettingsClick = { /* Open settings */ }
                )
            }

            composable<EditProfile> {
                ProfileFormScreen(
                    initialProfile = user,
                    mode = ProfileFormMode.EDIT,
                    onSave = { updatedUser ->
                        user = updatedUser
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onProfilePictureClick = { /* Open profile photo picker */ }
                )
            }
        }
    }
}
