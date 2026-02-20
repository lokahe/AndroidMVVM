package com.lokahe.androidmvvm.data.remote

import com.lokahe.androidmvvm.data.models.supabase.Post
import com.lokahe.androidmvvm.data.models.supabase.PostRequest
import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.CodeExchangeRequest
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.Profile
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.SetPasswordRequest
import com.lokahe.androidmvvm.data.models.supabase.SignRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    // Supabase
    @POST("auth/v1/token?grant_type=id_token")
    suspend fun googleAuth(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: GoogleAuth
    ): Response<GoogleAuthResponse>

    @POST("auth/v1/token?grant_type=pkce")
    suspend fun codeExchange(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: CodeExchangeRequest
    ): Response<AuthResponse>

    @GET("auth/v1/authorize")
    suspend fun xAuth(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Query("provider") provider: String = "x",
        @Query("redirect_to") redirectTo: String = Api.REDIRECT
    ): Response<Any>

    @GET("auth/v1/user")
    suspend fun varifyToken(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String
    ): Response<User>

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: RefreshTokenRequest
    ): Response<AuthResponse>

    @POST("auth/v1/logout")
    suspend fun signOut(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String
    ): Response<Any>

    @POST("auth/v1/otp")
    suspend fun sign( //opt
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: OtpRequest
    ): Response<Any>

    @PUT("auth/v1/user")
    suspend fun setPassword(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: SetPasswordRequest
    ): Response<Any>

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: SignRequest
    ): Response<Any>

    @POST("auth/v1/verify")
    suspend fun verifyEmail(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: VerifyRequest
    ): Response<AuthResponse>

    /*** REST public scheme ***/

    @GET("rest/v1/profiles")
    suspend fun fetchProfileById(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Query("select") select: String = "*",
        @Header("Accept") accept: String = "application/vnd.pgrst.object+json"
    ): Response<Profile>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body updatedFields: Map<String, Any?> // Use a Map to send only specific columns
    ): Response<List<Profile>>

    @POST("rest/v1/posts")
    suspend fun insertPost(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "return=representation", // Returns the inserted row
        @Body body: PostRequest
    ): Response<List<Post>>

//    @GET("rest/v1/posts")
//    suspend fun fetchPosts(
//        @Header("apikey") apiKey: String = Api.ANON_KEY,
//        @Header("Authorization") token: String,
//        @Query("reply_post_id") replyId: String, // e.g., "eq.00..000"
//        @Query("limit") limit: Int,               // Page Size
//        @Query("offset") offset: Int,             // Starting point
//        @Query("order") order: String = "created_at.desc", // Best practice for pagination
//        @Query("select") columns: String = "*, profiles(name, avatar), likes(count)"
//    ): Response<List<Post>>

    @GET("rest/v1/posts")
    suspend fun fetchPosts(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String,
        @Query("id") id: String = "neq.${Api.EMPTY_UUID}",
        @Query("author_id") authorId: String? = null,
        @Query("reply_post_id") replyId: String, // e.g., "eq.00..000"
        @Query("limit") limit: Int,               // Page Size
        @Query("offset") offset: Int,             // Starting point
        @Query("order") order: String = "created_at.desc", // Best practice for pagination
        @Query("select") columns: String = "*, profiles!Post_authorId_fkey(name, avatar), likes(count)"
    ): Response<List<Post>>

}