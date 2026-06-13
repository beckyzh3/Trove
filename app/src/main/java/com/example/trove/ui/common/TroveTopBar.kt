package com.example.trove.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroveTopBar(
    title: String = "Trove",
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    // The Slot API: We leave an empty space for the parent to inject whatever buttons they want!
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Navigate back")
                }
            }
        },
        actions = actions, // Pass the slot directly to the TopAppBar
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}


@Preview(showBackground = true)
@Composable
fun TroveTopBarPreview() {
    TroveTopBar(
        title = "Settings",
        onBack = {},
        showBack = true
    )
}