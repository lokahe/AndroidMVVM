package com.lokahe.http

import com.lokahe.log.L
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * Generic wrapper for a suspend Retrofit call: emits [ApiResult.Loading] first,
 * then [ApiResult.Success] / [ApiResult.Failure] / [ApiResult.Exception].
 * Contains no business logic.
 */
fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<ApiResult<T>> {
    return flow {
        try {
            emit(ApiResult.Loading)
            val response = apiCall()
            if (response.isSuccessful) {
                L.d("safeApiCall") { "response[isSuccessful]: $response" }
                @Suppress("UNCHECKED_CAST")
                if (response.body() == null) emit(ApiResult.Success(Unit as T))
                else emit(ApiResult.Success(response.body()!!))
            } else {
                L.d("safeApiCall") { "response: $response" }
                emit(ApiResult.Failure(parseError(response.code(), response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            L.d("safeApiCall", e) { "e: ${e.message}" }
            emit(ApiResult.Exception(e))
        }
    }
}

/**
 * Parse an error body in either envelope shape:
 * - Supabase-ish flat:   {"code":401,"error_code":"...","msg":"..."}
 * - Rovortex nested:     {"error":{"code":"machine_code","message":"..."}}
 */
fun parseError(httpCode: Int, errorBody: String?): ApiError {
    val fallback = ApiError(httpCode, "unknown", "Unknown error")
    if (errorBody.isNullOrBlank()) return fallback
    return try {
        val gson = Gson()
        val envelope = gson.fromJson(errorBody, ApiError.Envelope::class.java)
        if (envelope?.error?.code != null || envelope?.error?.message != null) {
            ApiError(
                code = httpCode,
                errorCode = envelope.error.code ?: "unknown",
                message = envelope.error.message ?: "Unknown error"
            )
        } else {
            gson.fromJson(errorBody, ApiError::class.java) ?: fallback
        }
    } catch (e: Exception) {
        fallback
    }
}