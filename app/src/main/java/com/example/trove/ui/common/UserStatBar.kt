package com.example.trove.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trove.User

@Composable
fun UserStatBar(
    user: User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserStat(
                number = user.numJournals,
                label = "Journals",
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.height(50.dp),
                thickness = 2.dp,
                color = Color.Gray
            )

            UserStat(
                number = user.countriesList.size,
                label = "Countries",
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.height(50.dp),
                thickness = 2.dp,
                color = Color.Gray
            )

            UserStat(
                number = user.likes,
                label = "Likes",
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.height(50.dp),
                thickness = 2.dp,
                color = Color.Gray
            )

            UserStat(
                number = user.friends.size,
                label = "Friends",
                modifier = Modifier.weight(1f)
            )

        }
    }
}

@Composable
private fun UserStat(
    number: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number.toString(),
            fontSize = 28.sp
        )

        Text(
            text = label,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserStatBarPreview() {
    UserStatBar(
        user = User(
            username = "beckzh3",
            name = "becky",
            bio = "hiii",
            countriesList = mutableListOf("Italy", "Greece", "Spain"),
            friends = mutableListOf("Bob", "Kristen", "Sophie"),
            numJournals = 3,
            likes = 558
        ),
        modifier = Modifier.fillMaxWidth()
    )
}