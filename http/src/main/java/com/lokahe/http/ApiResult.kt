package com.lokahe.http

import com.google.gson.annotations.SerializedName

sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val error: ApiError) : ApiResult<Nothing>()
    data class Exception(val exception: Throwable) : ApiResult<Nothing>()

    // Chaining function for Success
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }

    // Chaining function for custom ApiError (403, 404, etc)
    inline fun onFailure(action: (ApiError) -> Unit): ApiResult<T> {
        if (this is Failure) action(error)
        return this
    }

    // Chaining function for actual crashes/timeouts
    inline fun onException(action: (Throwable) -> Unit): ApiResult<T> {
        if (this is Exception) action(exception)
        return this
    }
}

data class ApiError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("error_code")
    val errorCode: String,
    @SerializedName("msg")
    val message: String
)