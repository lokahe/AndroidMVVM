package com.lokahe.androidmvvm.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Account(val id: String? = null) : Screen

    @Serializable
    data object Persons : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object SendPost : Screen
}