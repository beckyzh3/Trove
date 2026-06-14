package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.User


// INCOMPLETE JUST A PLACEHOLDER
@Composable
fun HomeScreen(
    user: User,
    onProfile: () -> Unit,
    onCreateJournal: () -> Unit
    ) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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


}

