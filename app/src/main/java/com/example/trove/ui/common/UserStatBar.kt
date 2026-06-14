package com.example.trove.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.User

@Composable
fun UserStatBar(
    user: User,
    modifier: Modifier = Modifier
) {

    Card (
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(Modifier.padding(24.dp)) {
            Text("${user.numJournals}")
            Spacer(modifier = Modifier.width(8.dp))
            Text("${user.countriesList.count()}")
            Spacer(modifier = Modifier.width(8.dp))
            Text("${user.likes}")
            Spacer(modifier = Modifier.width(8.dp))
            Text("${user.friends.count()}")
        }
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
            numJournals = 3
        )
    )
}