package com.lokahe.backend.rovortex.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class ChangePasswordRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)

/** Shape returned by register / login / refresh and the OAuth callback JSON. */
data class AuthResponse(
    @SerializedName("user") val user: RovortexUser?,
    @SerializedName("token") val token: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("refresh_expires_at") val refreshExpiresAt: String?
)

data class RovortexUser(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("email_verified") val emailVerified: Boolean?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/** GET /api/users/me — also returned by PATCH /api/users/me/profile */
data class MeResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("email_verified") val emailVerified: Boolean?,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("website_url") val websiteUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/** PATCH /api/users/me/profile — double-Option semantics: absent = keep, null = clear. */
data class ProfilePatch(
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("website_url") val websiteUrl: String? = null
)

data class PasswordResetRequest(
    @SerializedName("email") val email: String
)

/** POST /api/auth/signup/request — ask the server to email the verification. */
data class SignupRequest(
    @SerializedName("email") val email: String
)

/** POST /api/auth/signup/confirm-code — 6-digit code channel (App path). */
data class SignupConfirmCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String,
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String?
)

/** GET /api/users/me/identities */
data class IdentityResponse(
    @SerializedName("provider") val provider: String,
    @SerializedName("email") val email: String?,
    @SerializedName("has_raw_profile") val hasRawProfile: Boolean?,
    @SerializedName("created_at") val createdAt: String?
)

/** GET /api/users/me/sessions */
data class SessionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("device_label") val deviceLabel: String?,
    @SerializedName("ua") val ua: String?,
    @SerializedName("ip") val ip: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("expires_at") val expiresAt: String?
)
