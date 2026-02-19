package com.lokahe.androidmvvm.data.models.supabase

import com.google.gson.annotations.SerializedName

data class SignRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String?,
    @SerializedName("options")
    val options: SignUpOptions?
)

data class SignUpOptions(
    @SerializedName("data")
    val data: Map<String, Any> // Use a Map or a specific Metadata class
)

data class OtpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("create_user")
    val createUser: Boolean = true
)

data class SetPasswordRequest(
    @SerializedName("password")
    val password: String
)
data class VerifyRequest(
    @SerializedName("type")
    val type: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("token")
    val token: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)