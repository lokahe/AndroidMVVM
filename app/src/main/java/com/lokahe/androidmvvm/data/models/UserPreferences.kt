package com.lokahe.androidmvvm.data.models

data class UserPreferences(
    val userName: String,
    val userAge: Int,
    val userGender: String,
    val isLoggedIn: Boolean,

    val useAvatarColor: Boolean,
    val darkMode: Int
)