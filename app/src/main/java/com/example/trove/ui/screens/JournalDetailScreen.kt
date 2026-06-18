package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.Journal
import com.example.trove.JournalEntry
import com.example.trove.R
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.theme.TroveTheme

@Composable
fun JournalDetailScreen(
    journal: Journal,
    isOwner: Boolean,
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {

    // display one journal and its entries

    Scaffold (
        topBar = {
            TroveTopBar(
                title = journal.name,
                showBack = true,
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (isOwner) {
                FloatingActionButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit journal"
                    )
                }
            }
        }
    ){ innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Text(
                    text = journal.location,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = journal.routeSummary,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // show every journal entry

            items(journal.entries) { entry ->

                Card {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = entry.dateRange,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = entry.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JournalDetailScreenPreview() {
    TroveTheme {
        JournalDetailScreen(
            journal = Journal(
                id = "1",
                name = "Greece and Italy",
                location = "Athens, Rome, Florence",
                routeSummary = "Athens → Rome → Florence",
                likes = 558,
                entries = listOf(
                    JournalEntry(
                        dateRange = "May 20–22",
                        text = "Explored Athens and visited the Acropolis."
                    ),
                    JournalEntry(
                        dateRange = "May 23–25",
                        text = "Traveled to Rome and visited the Colosseum."
                    ),
                    JournalEntry(
                        dateRange = "May 26–28",
                        text = "Finished the trip in Florence and explored the city."
                    )
                )
            ),
            isOwner = true,
            onBack = {},
            onEditClick = {}
        )
    }
}