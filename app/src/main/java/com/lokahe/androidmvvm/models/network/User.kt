package com.lokahe.androidmvvm.models.network

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("description")
    val description: String?,
)

data class UsersResponse(
    @SerializedName("users")
    val users: List<User>,
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val code: String
)