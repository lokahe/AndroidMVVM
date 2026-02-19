package com.lokahe.androidmvvm.data.repository

import android.util.Log
import com.google.gson.Gson
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.ApiError
import com.lokahe.androidmvvm.data.models.supabase.ApiResult
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import com.lokahe.androidmvvm.data.remote.ApiService
import jakarta.inject.Inject
import retrofit2.Response

class HttpRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = apiCall()
            Log.d("safeApiCall", "response: $response")
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                // Parse errorBody to your data class
                val errorBody = response.errorBody()?.string()
                val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    ?: ApiError(response.code(), "unknown", "Unknown error")
                ApiResult.Failure(apiError)
            }
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
    }

    suspend fun gAuth(body: GoogleAuth): ApiResult<GoogleAuthResponse> =
        safeApiCall { apiService.googleAuth(body = body) }

    suspend fun varifyToken(accessToken: String): ApiResult<User> =
        safeApiCall { apiService.varifyToken(token = "Bearer $accessToken") }

    suspend fun refreshToken(refreshToken: String): ApiResult<AuthResponse> =
        safeApiCall { apiService.refreshToken(body = RefreshTokenRequest(refreshToken)) }

    suspend fun signOut(accessToken: String): ApiResult<Any> =
        safeApiCall { apiService.signOut(token = "Bearer $accessToken") }

    suspend fun sign(email: String): ApiResult<Any> =
        safeApiCall { apiService.sign(body = OtpRequest(email)) }

    suspend fun verifyEmail(email: String, token: String): ApiResult<AuthResponse> =
        safeApiCall { apiService.verifyEmail(body = VerifyRequest("email", email, token)) }
}
