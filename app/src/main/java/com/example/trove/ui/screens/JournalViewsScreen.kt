package com.example.trove.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.Journal
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.JournalEntry
import com.example.trove.ui.theme.TroveTheme

@Composable
fun JournalViewsScreen(
    journals: List<Journal>,
    onJournalClick: (Journal) -> Unit
) {

    // show all journals in a list

    Scaffold { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(
                items = journals,
                key = { it.id }
            ) { journal ->

                JournalPreviewCard(
                    journal = journal,
                    onClick = {
                        onJournalClick(journal)
                    }
                )
            }
        }
    }
}

@Composable
fun JournalPreviewCard(
    journal: Journal,
    onClick: () -> Unit
) {

    // preview card for one journal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = journal.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = journal.location,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Text(
                text = journal.routeSummary,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${journal.likes} likes",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun JournalDetailScreen(
    journal: Journal
) {

    // display one journal and its entries

    Scaffold { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Text(
                    text = journal.name,
                    style = MaterialTheme.typography.headlineMedium
                )

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
                        modifier = Modifier.padding(16.dp)
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
fun JournalViewsScreenPreview() {
    TroveTheme {
        JournalViewsScreen(
            journals = listOf(
                Journal(
                    id = "1",
                    name = "Greece and Italy",
                    location = "Athens, Rome, Florence",
                    routeSummary = "Athens → Rome → Florence",
                    likes = 558
                ),
                Journal(
                    id = "2",
                    name = "Japan Adventure",
                    location = "Tokyo, Kyoto, Osaka",
                    routeSummary = "Tokyo → Kyoto → Osaka",
                    likes = 240
                ),
                Journal(
                    id = "3",
                    name = "Summer in Spain",
                    location = "Barcelona and Madrid",
                    routeSummary = "Barcelona → Madrid",
                    likes = 120
                )
            ),
            onJournalClick = {}
        )
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
            )
        )
    }
}