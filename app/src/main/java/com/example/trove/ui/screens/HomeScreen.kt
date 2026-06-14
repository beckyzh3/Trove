package com.example.trove.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.trove.User


// INCOMPLETE JUST A PLACEHOLDER
@Composable
fun HomeScreen(
    user: User,
    onProfile: () -> Unit,
    onCreateJournal: () -> Unit
    ) {

    Button(
        onClick = onProfile
    ) {
        Text("Profile")
    }

    Button(
        onClick = onCreateJournal
    ) {
        Text("Create Journal")
    }

}

