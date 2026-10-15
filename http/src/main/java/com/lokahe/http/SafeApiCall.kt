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
                // Parse errorBody to your data class
                val errorBody = response.errorBody()?.string()
                val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    ?: ApiError(response.code(), "unknown", "Unknown error")
                emit(ApiResult.Failure(apiError))
            }
        } catch (e: Exception) {
            L.d("safeApiCall", e) { "e: ${e.message}" }
            emit(ApiResult.Exception(e))
        }
    }
}