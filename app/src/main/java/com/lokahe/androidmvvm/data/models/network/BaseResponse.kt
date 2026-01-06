package com.lokahe.androidmvvm.data.models.network

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("code")
    val code: String?
)