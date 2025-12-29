package com.lokahe.androidmvvm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

val SIDE_MENU_ITEMS = listOf(
    Triple(Screen.Home, Icons.Filled.Home, s(R.string.home)),
    Triple(Screen.Account, Icons.Filled.Person, s(R.string.account)),
    Triple(Screen.Persons, Icons.Filled.People, s(R.string.persons)),
    Triple(Screen.Settings, Icons.Filled.Settings, s(R.string.settings))
)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Account : Screen("account")
    object Persons : Screen("persons")
    object Settings : Screen("settings")
}