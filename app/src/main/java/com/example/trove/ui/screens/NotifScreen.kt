package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.ui.common.TroveTopBar

data class NotificationItem(
    val message: String
)

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {

    // sample notifications

    val notifications = listOf(
        NotificationItem("alice liked your journal"),
        NotificationItem("sam followed you"),
        NotificationItem("new comment on italy adventure"),
        NotificationItem("emma liked your photo"),
        NotificationItem("your journal reached 100 likes")
    )

    Scaffold(
        topBar = {
            TroveTopBar(
                title = "Notifications",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(notifications) { notification ->

                NotificationCard(
                    notification.message
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    message: String
) {

    Card(
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}