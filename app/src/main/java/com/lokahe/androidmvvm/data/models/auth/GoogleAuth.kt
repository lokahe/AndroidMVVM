package com.lokahe.androidmvvm.data.models.auth

import com.google.gson.annotations.SerializedName
import com.lokahe.androidmvvm.data.models.supabase.User

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
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("user")
    val user: User
)