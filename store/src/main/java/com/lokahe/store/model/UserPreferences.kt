package com.lokahe.store.model

data class UserPreferences(
    val userName: String,
    val userAge: Int,
    val userGender: String,
    val isLoggedIn: Boolean,

    val useAvatarColor: Boolean,
    val darkMode: Int
)