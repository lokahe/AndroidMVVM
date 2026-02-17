package com.lokahe.androidmvvm.data.models.x

import com.google.gson.annotations.SerializedName

data class Oauth(
    @SerializedName("oauth_token")
    val oauthToken: String,
    @SerializedName("oauth_token_secret")
    val oauthTokenSecret: String,
    @SerializedName("oauth_callback_confirmed")
    val oauthCallbackConfirmed: Boolean,

    // base
    @SerializedName("message")
    val message: String?,

    @SerializedName("code")
    val code: String?
)