package com.example.trove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trove.User
import com.example.trove.UserSaver


@Composable
fun UserNavigation() {
    var user by rememberSaveable(stateSaver = UserSaver) {
        mutableStateOf(User())
    }

    val navController = rememberNavController()
}