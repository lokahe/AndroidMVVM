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