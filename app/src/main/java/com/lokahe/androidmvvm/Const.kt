package com.lokahe.androidmvvm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

val SIDE_MENU_ITEMS = listOf(
    Pair(R.string.home, Icons.Filled.Home),
    Pair(R.string.account, Icons.Filled.Person),
    Pair(R.string.persons, Icons.Filled.People),
    Pair(R.string.settings, Icons.Filled.Settings)
)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Account : Screen("account")
    object Persons : Screen("persons")
    object Settings : Screen("settings")
}