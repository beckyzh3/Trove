package com.example.trove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trove.Journal
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
        mutableStateOf(User())
    }

    var journal by remember {
        mutableStateOf(Journal())
    }


    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                user = user,
                onProfile = {
                    navController.navigate(Profile)
                },
                onCreateJournal = {
                    navController.navigate(CreateJournal)
                }
            )
        }

        composable<Profile> {
            ProfileScreen(
                user = user,
                onBack = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(EditProfile)
                },
                onSettingsClick = {
                    // connect settings later
                }
            )
        }

        composable<CreateProfile> {
            ProfileFormScreen(
                initialProfile = user,
                mode = ProfileFormMode.CREATE,
                onSave = { createdUser ->
                    user = createdUser
                    navController.navigate(Profile) {
                        popUpTo(CreateProfile) {
                            inclusive = true
                        }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                },
                onProfilePictureClick = {
                    // connect photo picker later
                }
            )
        }

        composable<EditProfile> {
            ProfileFormScreen(
                initialProfile = user,
                mode = ProfileFormMode.EDIT,
                onSave = {updatedUser ->
                    user = updatedUser
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                },
                onProfilePictureClick = {
                    // do later
                }
            )
        }

        composable<CreateJournal> {
            JournalFormScreen(
                initialJournal = Journal(),
                mode = JournalFormMode.CREATE,
                onSave = { createdJournal ->
                    journal = createdJournal
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                },
                onAddPhotoClick =  {
                    // do later
                }
            )
        }

        composable<EditJournal> {
            JournalFormScreen(
                initialJournal = journal,
                mode = JournalFormMode.EDIT,
                onSave = { updatedJournal ->
                    journal = updatedJournal
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                },
                onAddPhotoClick = {
                    // do later
                }
            )
        }

    }

}