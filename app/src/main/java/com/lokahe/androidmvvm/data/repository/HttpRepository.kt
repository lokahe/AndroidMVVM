package com.lokahe.androidmvvm.data.repository

import android.util.Log
import com.google.gson.Gson
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.supabase.ApiError
import com.lokahe.androidmvvm.data.models.supabase.ApiResult
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.CodeExchangeRequest
import com.lokahe.androidmvvm.data.models.supabase.FollowRequest
import com.lokahe.androidmvvm.data.models.supabase.LikeRequest
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.PostRequest
import com.lokahe.androidmvvm.data.models.supabase.Profile
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import com.lokahe.androidmvvm.data.remote.Api
import com.lokahe.androidmvvm.data.remote.ApiService
import com.lokahe.androidmvvm.data.remote.b
import com.lokahe.androidmvvm.data.remote.eq
import com.lokahe.androidmvvm.emptyNull
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class HttpRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<ApiResult<T>> {
        return flow {
            try {
                emit(ApiResult.Loading)
                val response = apiCall()
                if (response.isSuccessful) {
                    Log.d("safeApiCall", "response[isSuccessful]: $response")
                    @Suppress("UNCHECKED_CAST")
                    if (response.body() == null) emit(ApiResult.Success(Unit as T))
                    else emit(ApiResult.Success(response.body()!!))
                } else {
                    Log.d("safeApiCall", "response: $response")
                    // Parse errorBody to your data class
                    val errorBody = response.errorBody()?.string()
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                        ?: ApiError(response.code(), "unknown", "Unknown error")
                    emit(ApiResult.Failure(apiError))
                }
            } catch (e: Exception) {
                Log.d("safeApiCall", "e: ${e.message}")
                emit(ApiResult.Exception(e))
            }
        }
    }

    fun gAuth(body: GoogleAuth): Flow<ApiResult<AuthResponse>> =
        safeApiCall { apiService.googleAuth(body = body) }

    fun codeExchange(code: String, codeVerifier: String): Flow<ApiResult<AuthResponse>> =
        safeApiCall { apiService.codeExchange(body = CodeExchangeRequest(code, codeVerifier)) }

    fun varifyToken(token: String): Flow<ApiResult<User>> =
        safeApiCall { apiService.varifyToken(token = token.b) }

    fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResponse>> =
        safeApiCall { apiService.refreshToken(body = RefreshTokenRequest(refreshToken)) }

    fun signOut(token: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.signOut(token = token.b) }

    fun sign(email: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.sign(body = OtpRequest(email)) }

    fun verifyEmail(email: String, token: String): Flow<ApiResult<AuthResponse>> =
        safeApiCall { apiService.verifyEmail(body = VerifyRequest("email", email, token)) }

    fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>> =
        safeApiCall { apiService.fetchProfileById(token = token.b, id = id.eq) }

    fun insertPost(token: String, post: PostRequest): Flow<ApiResult<List<Post>>> =
        safeApiCall { apiService.insertPost(token = token.b, body = post) }

    fun fetchPosts(
        token: String,
        authorId: String? = null,
        replyId: String = Api.EMPTY_UUID,
        limit: Int = Api.PAGE_SIZE,
        offset: Int = 0
    ): Flow<ApiResult<List<Post>>> =
        safeApiCall {
            apiService.fetchPosts(
                token = token.b,
                authorId = authorId?.emptyNull()?.let { authorId.eq },
                replyId = replyId.eq,
                limit = limit,
                offset = offset
            )
        }

    fun deletePosts(token: String, ids: List<String>): Flow<ApiResult<Any>> =
        safeApiCall {
            apiService.deletePosts(token = token.b, inCondition = "in.(${ids.joinToString(",")})")
        }

    fun follow(token: String, followerId: String, targetId: String): Flow<ApiResult<Any>> =
        safeApiCall {
            apiService.follow(token = token.b, body = FollowRequest(followerId, targetId))
        }

    fun unFollow(token: String, followerId: String, targetId: String): Flow<ApiResult<Any>> =
        safeApiCall {
            apiService.unFollow(token = token.b, followerId = followerId.eq, targetId = targetId.eq)
        }

    fun like(token: String, postId: String, userId: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.like(token = token.b, body = LikeRequest(postId, userId)) }

    fun dislike(token: String, postId: String, userId: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.dislike(token = token.b, postId = postId.eq, userId = userId.eq) }
}