package com.example.trove.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.User

@Composable
fun UserProfileCard(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ){

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                UserProfilePicture()

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = user.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(text = "@${user.username}")

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = user.bio)

                Spacer(modifier = Modifier.height(8.dp))

                UserStatBar(
                    user = user,
                    modifier = Modifier.padding(16.dp)
                )
            }

            OutlinedButton(
                onClick = { onEditClick() },
                modifier = Modifier.align(Alignment.TopEnd),
                ) {
                Text(text = "Edit Profile")
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun UserProfileCardPreview() {
    UserProfileCard(
        user = User(
            username = "beckzh3",
            name = "Becky Zheng",
            bio = "hiii",
            countriesList = mutableListOf("Italy", "Greece", "Spain"),
            friends = mutableListOf("Bob", "Kristen", "Sophie"),
            numJournals = 3,
            likes = 558
        ),
        onEditClick = {},
        modifier = Modifier.padding(16.dp)
    )
}