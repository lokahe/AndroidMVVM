package com.lokahe.androidmvvm.data.remote

import com.lokahe.androidmvvm.data.models.auth.GoogleAuth
import com.lokahe.androidmvvm.data.models.auth.GoogleAuthResponse
import com.lokahe.androidmvvm.data.models.supabase.AuthResponse
import com.lokahe.androidmvvm.data.models.supabase.OtpRequest
import com.lokahe.androidmvvm.data.models.supabase.RefreshTokenRequest
import com.lokahe.androidmvvm.data.models.supabase.SetPasswordRequest
import com.lokahe.androidmvvm.data.models.supabase.SignRequest
import com.lokahe.androidmvvm.data.models.supabase.VerifyRequest
import com.lokahe.androidmvvm.data.models.x.Oauth
import com.lokahe.androidmvvm.utils.Utils
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    // X (Twitter)
    @POST(Api.X_OAUTH)
    suspend fun xOauth(
        @Header("Authorization") authorization: String = "OAuth " +
                /*        @HeaderMap params: Map<String, String> =*/
                mapOf(
                    "oauth_consumer_key" to Api.TWITTER_CONSUMER_KEY,
                    "oauth_nonce" to java.util.UUID.randomUUID().toString().replace("-", ""),
                    "oauth_signature_method" to "HMAC-SHA1",
                    "oauth_timestamp" to (System.currentTimeMillis() / 1000).toString(),
                    "oauth_version" to "1.0",
                    "oauth_callback" to Utils.percentEncode("Api.BACKENDLESS_TWITTER_CALL_BACK"),   //
                ).let {
                    it.plus(
                        "oauth_signature" to Utils.generateOAuthSignature(
                            "POST",
                            "https://api.x.com/oauth/request_token",
                            it, Api.TWITTER_CONSUMER_KEY_SECRET
                        )
                    )
                }.entries.sortedBy { it.key }.joinToString(",") { "${it.key}=${it.value}" },
    ): Response<Oauth>

    // Supabase
    @POST("auth/v1/token?grant_type=id_token")
    suspend fun googleAuth(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Content-Type") contentType: String = "application/json",
        @Body body: GoogleAuth
    ): Response<GoogleAuthResponse>

    @GET("auth/v1/user")
    suspend fun varifyToken(
        @Header("apikey") apiKey: String = Api.ANON_KEY,
        @Header("Authorization") token: String
    ): Response<com.lokahe.androidmvvm.data.models.supabase.User>

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