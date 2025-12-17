package com.lokahe.androidmvvm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

val SIDE_MENU_ITEMS = listOf(
    Pair(R.string.home, Icons.Filled.Home),
    Pair(R.string.account, Icons.Filled.Person),
    Pair(R.string.notifications, Icons.Filled.Notifications),
    Pair(R.string.settings, Icons.Filled.Settings)
)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Account : Screen("account")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
}