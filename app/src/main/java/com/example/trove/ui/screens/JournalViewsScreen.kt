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
import com.example.trove.ui.common.JournalPreviewCard
import com.example.trove.ui.common.TroveTopBar
import com.example.trove.ui.theme.TroveTheme

@Composable
fun JournalViewsScreen(
    journals: List<Journal>,
    onJournalClick: (Journal) -> Unit,
    onBack: () -> Unit
) {

    // show all journals in a list

    Scaffold (
        topBar = {
            TroveTopBar(
                title = "Journals",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->

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
            onJournalClick = {},
            onBack = {}
        )
    }
}
