package com.example.trove.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    isFriend: Boolean,
    journalCount: Int,
    countryCount: Int,
    totalLikes: Int,
    friendCount: Int,
    isCurrentUser: Boolean,
    onEditClick: () -> Unit,
    onAddFriendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                UserProfilePicture(
                    profilePicture = user.profilePicture,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = user.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(text = "@${user.username}")

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = user.bio)

                Spacer(modifier = Modifier.height(8.dp))

                UserStatBar(
                    journalCount = journalCount,
                    countryCount = countryCount,
                    totalLikes = totalLikes,
                    friendCount = friendCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            if (isCurrentUser) {
                OutlinedButton(
                    onClick = { onEditClick() },
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Text(text = "Edit Profile")
                }
            } else {
                OutlinedButton(
                    onClick = onAddFriendClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        if (isFriend) {
                            "Friends"
                        } else {
                            "Add Friend"
                        }
                    )
                }
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
        ),
        isFriend = false,
        journalCount = 3,
        countryCount = 3,
        totalLikes = 558,
        friendCount = 3,
        isCurrentUser = true,
        onEditClick = {},
        onAddFriendClick = {},
        modifier = Modifier.padding(16.dp)
    )
}