package com.example.trove.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trove.Journal
import com.example.trove.ui.common.JournalPreviewCard
import com.example.trove.ui.common.TroveTopBar

@Composable
fun ExpScreen(
    journals: List<Journal>,
    onJournalClick: (Journal) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Journals") }

    val publicJournals = journals.filter { it.isPublic }

    val searchResults = publicJournals.filter { journal ->
        when (selectedFilter) {
            "Places" -> journal.location.contains(searchQuery, ignoreCase = true)
            "People" -> journal.ownerName.contains(searchQuery, ignoreCase = true)
            else -> journal.name.contains(searchQuery, ignoreCase = true) ||
                    journal.location.contains(searchQuery, ignoreCase = true) ||
                    journal.ownerName.contains(searchQuery, ignoreCase = true)
        }
    }

    val trendingJournals = publicJournals.sortedByDescending { it.likes }

    Scaffold(
        topBar = {
            TroveTopBar(
                title = "Explore"
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text("Search journals places and people")
                    },
                    shape = MaterialTheme.shapes.extraLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterButton(
                        label = "Journals",
                        onClick = { selectedFilter = "Journals" }
                    )

                    FilterButton(
                        label = "Places",
                        onClick = { selectedFilter = "Places" }
                    )

                    FilterButton(
                        label = "People",
                        onClick = { selectedFilter = "People" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "Search Results")
            }

            items(
                items = searchResults,
                key = { it.id }
            ) { journal ->
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    JournalPreviewCard(
                        journal = journal,
                        onClick = {
                            onJournalClick(journal)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "Trending Journals")

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(
                        items = trendingJournals,
                        key = { it.id }
                    ) { journal ->
                        TrendingJournalCard(journal = journal)
                    }
                }
            }
        }
    }
}