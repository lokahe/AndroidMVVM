package com.lokahe.androidmvvm.models.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val login: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("user-token")
    val userToken: String?,

    @SerializedName("lastLogin")
    val lastLogin: Long?,

    @SerializedName("userStatus")
    val userStatus: String?,

    @SerializedName("created")
    val created: Long?,

    @SerializedName("accountType")
    val accountType: String?,

    @SerializedName("socialAccount")
    val socialAccount: String?,

    @SerializedName("ownerId")
    val ownerId: String?,

    @SerializedName("oAuthIdentities")
    val oAuthIdentities: List<String>?,

    @SerializedName("name")
    val name: String,

    @SerializedName("___class")
    val className: String?,

    @SerializedName("blUserLocale")
    val blUserLocale: String?,

    @SerializedName("updated")
    val updated: Long?,

    @SerializedName("email")
    val email: String,

    @SerializedName("message")
    val message: String?,

    @SerializedName("code")
    val code: String?
)