package com.lokahe.androidmvvm.models.network

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("code")
    val code: String?
)