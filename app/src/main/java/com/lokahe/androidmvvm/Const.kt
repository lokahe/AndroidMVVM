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

val AVATAR_IDS = (10..49).toList()
val AVATARS = AVATAR_IDS.map { "https://picsum.photos/id/$it/200" }

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Account : Screen("account")
    object Persons : Screen("persons")
    object Settings : Screen("settings")
}

sealed class AppDialog {
    data object None : AppDialog()
    data object Logout : AppDialog()
    data object Login : AppDialog()
    data object Avatar : AppDialog()
    data object Loading : AppDialog()
    // Add more later easily: data object DeleteAccount : AppDialog()
}

