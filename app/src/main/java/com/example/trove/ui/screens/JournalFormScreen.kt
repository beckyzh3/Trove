package com.example.trove.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trove.Journal
import com.example.trove.JournalEntry
import com.example.trove.R
import com.example.trove.ui.common.TroveTopBar

enum class JournalFormMode {
    CREATE,
    EDIT
}

@Composable
fun JournalFormScreen(
    initialJournal: Journal,
    mode: JournalFormMode,
    onSave: (Journal) -> Unit,
    onCancel: () -> Unit,
    onAddPhotoClick: () -> Unit
) {

    var draftJournal by remember {
        mutableStateOf(initialJournal)
    }

    var entries by remember {
        mutableStateOf(
            if (initialJournal.entries.isEmpty()) {
                listOf(JournalEntry())
            } else {
                initialJournal.entries
            }
        )
    }

    var submitAttempted by rememberSaveable {
        mutableStateOf(false)
    }

    var showDiscardDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val hasUnsavedChanges =
        draftJournal != initialJournal ||
                entries != initialJournal.entries

    val isNameValid = draftJournal.name.isNotBlank()
    val isLocationValid = draftJournal.location.isNotBlank()

    val areEntriesValid = entries.all { entry ->
        entry.dateRange.isNotBlank() &&
                entry.text.isNotBlank()
    }

    val isFormValid =
        isNameValid &&
                isLocationValid &&
                entries.isNotEmpty() &&
                areEntriesValid

    val screenTitle = when (mode) {
        JournalFormMode.CREATE -> "Create Journal"
        JournalFormMode.EDIT -> "Edit Journal"
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = {
                showDiscardDialog = false
            },
            title = {
                Text("Discard changes?")
            },
            text = {
                Text("You haven't saved your journal.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onCancel()
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                    }
                ) {
                    Text("Keep editing")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TroveTopBar(
                title = screenTitle,
                showBack = true,
                onBack = {
                    if (hasUnsavedChanges) {
                        showDiscardDialog = true
                    } else {
                        onCancel()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    submitAttempted = true

                    if (isFormValid) {
                        onSave(
                            draftJournal.copy(
                                entries = entries
                            )
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = when (mode) {
                        JournalFormMode.CREATE -> "Create journal"
                        JournalFormMode.EDIT -> "Save journal"
                    }
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = draftJournal.name,
                onValueChange = {
                    draftJournal = draftJournal.copy(name = it)
                },
                label = {
                    Text("Journal name")
                },
                isError = submitAttempted && !isNameValid,
                supportingText = {
                    if (submitAttempted && !isNameValid) {
                        Text("Journal name cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = draftJournal.location,
                onValueChange = {
                    draftJournal = draftJournal.copy(location = it)
                },
                label = {
                    Text("Location")
                },
                isError = submitAttempted && !isLocationValid,
                supportingText = {
                    if (submitAttempted && !isLocationValid) {
                        Text("Location cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Journal Entries")

            entries.forEachIndexed { index, entry ->

                JournalEntryEditor(
                    entry = entry,
                    showErrors = submitAttempted,
                    onEntryChange = { updatedEntry ->
                        entries = entries.toMutableList().also {
                            it[index] = updatedEntry
                        }
                    },
                    onDeleteClick = {
                        entries = entries.toMutableList().also {
                            it.removeAt(index)
                        }
                    }
                )
            }

            Button(
                onClick = {
                    entries = entries + JournalEntry()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Entry")
            }

            OutlinedTextField(
                value = draftJournal.routeSummary,
                onValueChange = {
                    draftJournal =
                        draftJournal.copy(routeSummary = it)
                },
                label = {
                    Text("Route summary")
                },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onAddPhotoClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Photos")
            }
        }
    }
}

@Composable
fun JournalEntryEditor(
    entry: JournalEntry,
    showErrors: Boolean,
    onEntryChange: (JournalEntry) -> Unit,
    onDeleteClick: () -> Unit
) {
    val isDateValid = entry.dateRange.isNotBlank()
    val isTextValid = entry.text.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = entry.dateRange,
                onValueChange = {
                    onEntryChange(
                        entry.copy(dateRange = it)
                    )
                },
                label = {
                    Text("Date range")
                },
                isError = showErrors && !isDateValid,
                supportingText = {
                    if (showErrors && !isDateValid) {
                        Text("Date range cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = entry.text,
                onValueChange = {
                    onEntryChange(
                        entry.copy(text = it)
                    )
                },
                label = {
                    Text("Journal entry")
                },
                minLines = 4,
                isError = showErrors && !isTextValid,
                supportingText = {
                    if (showErrors && !isTextValid) {
                        Text("Journal entry cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = onDeleteClick
            ) {
                Text("Delete Entry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateJournalFormPreview() {
    JournalFormScreen(
        initialJournal = Journal(),
        mode = JournalFormMode.CREATE,
        onSave = {},
        onCancel = {},
        onAddPhotoClick = {}
    )
}


@Preview(showBackground = true)
@Composable
fun EditJournalFormPreview() {
    JournalFormScreen(
        initialJournal = Journal(
            name = "Greece and Italy",
            location = "Athens, Rome, Florence",
            entries = listOf(
                JournalEntry(
                    dateRange = "May 20–22",
                    text = "Explored Athens and visited the Acropolis."
                ),
                JournalEntry(
                    dateRange = "May 23–25",
                    text = "Traveled to Rome and visited the Colosseum."
                )
            ),
            routeSummary = "Athens → Rome → Florence"
        ),
        mode = JournalFormMode.EDIT,
        onSave = {},
        onCancel = {},
        onAddPhotoClick = {}
    )
}





