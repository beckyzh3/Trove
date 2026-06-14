package com.example.trove.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.R
import com.example.trove.User
import com.example.trove.UserSaver
import com.example.trove.ui.common.ProfilePicturePlaceholder
import com.example.trove.ui.common.TroveTopBar

enum class ProfileFormMode {
    CREATE,
    EDIT
}

@Composable
fun ProfileFormScreen(
    initialProfile: User,
    mode: ProfileFormMode,
    onSave: (User) -> Unit,
    onCancel: () -> Unit,
    onProfilePictureClick: () -> Unit
) {
    var draftProfile by rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf(initialProfile)
    }

    var showDiscardDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val hasUnsavedChanges = draftProfile != initialProfile

    val isNameValid = draftProfile.name.isNotBlank()

    val isUsernameValid =
        mode == ProfileFormMode.EDIT ||
                draftProfile.username.isNotBlank()

    val isFormValid =
        isNameValid && isUsernameValid

    var submitAttempted by rememberSaveable {
        mutableStateOf(false)
    }

    val screenTitle = when (mode) {
        ProfileFormMode.CREATE -> "Create Profile"
        ProfileFormMode.EDIT -> "Edit Profile"
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = {
                showDiscardDialog = false
            },
            title = {
                Text("Discard changes?")
            },
            text = {
                Text("You haven't saved your changes.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onCancel()
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                    }
                ) {
                    Text("Keep editing")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TroveTopBar(
                title = screenTitle,
                showBack = true,
                onBack = {
                    if (hasUnsavedChanges) {
                        showDiscardDialog = true
                    } else {
                        onCancel()
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    submitAttempted = true

                    if (isFormValid) {
                        onSave(draftProfile)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = when (mode) {
                        ProfileFormMode.CREATE -> "Create profile"
                        ProfileFormMode.EDIT -> "Save profile"
                    }
                )
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfilePicturePlaceholder(
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        onProfilePictureClick()
                    }
            )

            Text("Tap to change profile picture")

            OutlinedTextField(
                value = draftProfile.name,
                onValueChange = {
                    draftProfile = draftProfile.copy(name = it)
                },
                label = {
                    Text("Name")
                },
                isError = !isNameValid,
                supportingText = {
                    if (!isNameValid) {
                        Text("Name cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftProfile.username,
                onValueChange = {
                    if (mode == ProfileFormMode.CREATE) {
                        draftProfile =
                            draftProfile.copy(username = it)
                    }
                },
                label = {
                    Text("Username")
                },
                readOnly = mode == ProfileFormMode.EDIT,
                isError =
                    mode == ProfileFormMode.CREATE &&
                            !isUsernameValid,
                supportingText = {
                    when {
                        mode == ProfileFormMode.CREATE &&
                                !isUsernameValid -> {
                            Text("Username cannot be empty")
                        }

                        mode == ProfileFormMode.CREATE -> {
                            Text("Username cannot be changed later")
                        }

                        else -> {
                            Text("Username cannot be changed")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftProfile.email,
                onValueChange = {},
                label = {
                    Text("Email")
                },
                readOnly = true,
                supportingText = {
                    Text("Connected to your Google account")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftProfile.bio,
                onValueChange = {
                    draftProfile = draftProfile.copy(bio = it)
                },
                label = {
                    Text("Bio")
                },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProfileFormPreview() {
    ProfileFormScreen(
        initialProfile = User(
            name = "Becky Zheng",
            username = "",
            email = "becky@gmail.com",
            bio = ""
        ),
        mode = ProfileFormMode.CREATE,
        onSave = {},
        onCancel = {},
        onProfilePictureClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileFormPreview() {
    ProfileFormScreen(
        initialProfile = User(
            name = "Becky Zheng",
            username = "beckzh3",
            email = "becky@gmail.com",
            bio = "hiii"
        ),
        mode = ProfileFormMode.EDIT,
        onSave = {},
        onCancel = {},
        onProfilePictureClick = {}
    )
}