package com.lokahe.androidmvvm.ui.screens

import androidx.navigation3.runtime.NavKey
import com.lokahe.androidmvvm.data.models.supabase.Post
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

    @Serializable
    data class PostDetail(val post: Post) : Screen
}