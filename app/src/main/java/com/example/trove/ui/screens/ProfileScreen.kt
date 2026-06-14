package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.R
import com.example.trove.User
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.common.ImagePlaceholder
import com.example.trove.ui.common.UserProfileCard

@Composable
fun ProfileScreen(
    user: User,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TroveTopBar(
                title = "Profile",
                onBack = onBack,
                showBack = true,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ){
            UserProfileCard(
                user = user,
                onEditClick = onEditClick,
                modifier = Modifier.padding(innerPadding)
            )

            Text(text = "My Journals",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        24.dp,
                        alignment = Alignment.CenterHorizontally)
                ) {
                    ImagePlaceholder(
                        modifier = Modifier.size(140.dp)
                    )

                    ImagePlaceholder(
                        modifier = Modifier.size(140.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        24.dp,
                        alignment = Alignment.CenterHorizontally)
                ) {
                    ImagePlaceholder(
                        modifier = Modifier.size(140.dp)
                    )

                    ImagePlaceholder(
                        modifier = Modifier.size(140.dp)
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
            numJournals = 3,
            likes = 558
        ),
        onBack = {},
        onEditClick = {},
        onSettingsClick = {}
    )
}