package com.lokahe.androidmvvm.data.remote

import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.CodeExchangeRequest
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.SetPasswordRequest
import com.lokahe.androidmvvm.data.models.supabase.SignRequest
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

}