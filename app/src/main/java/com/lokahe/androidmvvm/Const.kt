package com.lokahe.androidmvvm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

const val PAGE_SIZE = 10
val SIDE_MENU_ITEMS = listOf(
    Triple(Screen.Home, Icons.Filled.Home, s(R.string.home)),
    Triple(Screen.Account, Icons.Filled.Person, s(R.string.account)),
    Triple(Screen.Persons, Icons.Filled.People, s(R.string.persons)),
    Triple(Screen.Settings, Icons.Filled.Settings, s(R.string.settings))
)

val GENDERS = listOf(
    Triple(s(R.string.female), "\u2640", 0xFFFF00FF),
    Triple(s(R.string.male), "\u2642", 0xFF0000FF),     // Blue
    Triple(s(R.string.transgender), "\u26A7", 0xFF55CDFC),   // Light blue (flag color)
    Triple(s(R.string.intersex), "\u26A5", 0xFFFFD800),   // Yellow (flag)
    Triple(s(R.string.lesbian), "\u26A2", 0xFFD52D00),   // Orange-red (flag)
    Triple(s(R.string.gay), "\u26A3", 0xFF0038A8),       // Blue (flag)
    Triple(s(R.string.nonbinary), "\u26B2", 0xFFFFF430),   // Yellow (flag)
    Triple(s(R.string.heterosexuality), "\u26A4", 0xFFFF00FF),
    Triple("", "", 0xFFFF00FF)
)

val AVATAR_IDS = (10..49).toList()
val AVATARS = AVATAR_IDS.map { "https://picsum.photos/id/$it/200" }

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Account : Screen("account")
    object Persons : Screen("persons")
    object Settings : Screen("settings")
    object SendPost : Screen("send_post")
}

sealed class AppDialog {
    data object None : AppDialog()
    data object Logout : AppDialog()
    data object Login : AppDialog()
    data object Avatar : AppDialog()
    data object Loading : AppDialog()
    // Add more later easily: data object DeleteAccount : AppDialog()
}

sealed class UserHeaderOption {
    data object None : UserHeaderOption()
    data object Sign : UserHeaderOption()
    data object Edit : UserHeaderOption()
    data object Send : UserHeaderOption()
}

