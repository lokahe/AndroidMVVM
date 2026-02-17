package com.lokahe.androidmvvm.data.models.auth

import com.google.gson.annotations.SerializedName

data class GoogleAuth(
    @SerializedName("id_token")
    val idToken: String,
    @SerializedName("provider")
    val provider: String = "google",
    @SerializedName("nonce")
    val nonce: String?
)

data class GoogleAuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("user")
    val user: GoogleAuthResponseUser
)

data class GoogleAuthResponseUser(
    @SerializedName("id")
    val id: String
)