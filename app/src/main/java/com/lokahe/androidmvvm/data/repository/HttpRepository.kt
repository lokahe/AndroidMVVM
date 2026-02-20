package com.lokahe.androidmvvm.data.repository

import android.util.Log
import com.google.gson.Gson
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.PostRequest
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.ApiError
import com.lokahe.androidmvvm.data.models.supabase.ApiResult
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.CodeExchangeRequest
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.Profile
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import com.lokahe.androidmvvm.data.remote.Api
import com.lokahe.androidmvvm.data.remote.ApiService
import com.lokahe.androidmvvm.emptyNull
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
            Log.d("safeApiCall", "e: ${e.message}")
            ApiResult.Exception(e)
        }
    }

    suspend fun gAuth(body: GoogleAuth): ApiResult<GoogleAuthResponse> =
        safeApiCall { apiService.googleAuth(body = body) }

    suspend fun codeExchange(code: String, codeVerifier: String): ApiResult<AuthResponse> =
        safeApiCall { apiService.codeExchange(body = CodeExchangeRequest(code, codeVerifier)) }

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

    suspend fun fetchProfileById(token: String, id: String): ApiResult<Profile> =
        safeApiCall { apiService.fetchProfileById(token = "Bearer $token", id = "eq.$id") }

    suspend fun insertPost(token: String, post: PostRequest): ApiResult<List<Post>> =
        safeApiCall { apiService.insertPost(token = "Bearer $token", body = post) }

    suspend fun fetchPosts(
        token: String,
        authorId: String? = null,
        replyId: String = Api.EMPTY_UUID,
        limit: Int = Api.PAGE_SIZE,
        offset: Int = 0
    ): ApiResult<List<Post>> =
        safeApiCall {
            apiService.fetchPosts(
                token = "Bearer $token",
                authorId = authorId?.emptyNull()?.let { "eq.$authorId" },
                replyId = "eq.$replyId",
                limit = limit,
                offset = offset
            )
        }

    suspend fun deletePost(token: String, id: String): ApiResult<Any> =
        safeApiCall { apiService.deletePost(token = "Bearer $token", id = "eq.$id") }
}
