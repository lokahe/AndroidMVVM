package com.lokahe.backend.rovortex

import com.lokahe.backend.rovortex.model.AuthRequest
import com.lokahe.backend.rovortex.model.AuthResponse
import com.lokahe.backend.rovortex.model.ChangePasswordRequest
import com.lokahe.backend.rovortex.model.EmailEntryConfirmRequest
import com.lokahe.backend.rovortex.model.EmailEntryRequest
import com.lokahe.backend.rovortex.model.IdentityResponse
import com.lokahe.backend.rovortex.model.MeResponse
import com.lokahe.backend.rovortex.model.PasswordResetRequest
import com.lokahe.backend.rovortex.model.ProfilePatch
import com.lokahe.backend.rovortex.model.RefreshRequest
import com.lokahe.backend.rovortex.model.RegisterRequest
import com.lokahe.backend.rovortex.model.SessionResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Rovortex gateway API. All errors share the envelope
 * {"error":{"code":"machine_code","message":"..."}} (parsed in :http).
 */
interface ApiService {

    @POST("api/users/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/users/login")
    suspend fun login(@Body body: AuthRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body body: RefreshRequest): Response<Unit>

    @POST("api/auth/password-reset/request")
    suspend fun requestPasswordReset(@Body body: PasswordResetRequest): Response<Unit>

    /**
     * Unified email entry: 200 = AuthResponse body (direct login); 202 = empty
     * body (verification email sent). Typed as raw ResponseBody because the
     * Gson converter chokes on the empty 202 body; the repository decides.
     */
    @POST("api/auth/email-entry")
    suspend fun emailEntry(@Body body: EmailEntryRequest): Response<ResponseBody>

    /** Code confirm: logs in (registered) or creates the account (unregistered). */
    @POST("api/auth/email-entry/confirm")
    suspend fun emailEntryConfirm(@Body body: EmailEntryConfirmRequest): Response<AuthResponse>

    @GET("api/users/me")
    suspend fun me(@Header("Authorization") bearerToken: String): Response<MeResponse>

    @GET("api/users/me/identities")
    suspend fun identities(@Header("Authorization") bearerToken: String): Response<List<IdentityResponse>>

    @GET("api/users/me/sessions")
    suspend fun sessions(@Header("Authorization") bearerToken: String): Response<List<SessionResponse>>

    @DELETE("api/users/me/sessions/{id}")
    suspend fun kickSession(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String
    ): Response<Unit>

    @PATCH("api/users/me/profile")
    suspend fun patchProfile(
        @Header("Authorization") bearerToken: String,
        @Body body: ProfilePatch
    ): Response<MeResponse>

    @PUT("api/users/me/password")
    suspend fun changePassword(
        @Header("Authorization") bearerToken: String,
        @Body body: ChangePasswordRequest
    ): Response<Unit>
}
