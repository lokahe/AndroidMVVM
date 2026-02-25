package com.lokahe.androidmvvm.data.models.supabase

import com.google.gson.annotations.SerializedName

sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val error: ApiError) : ApiResult<Nothing>()
    data class Exception(val exception: Throwable) : ApiResult<Nothing>()

    // Chaining function for Success
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }

    // Chaining function for custom ApiError (403, 404, etc)
    inline fun onFailure(action: (ApiError) -> Unit): ApiResult<T> {
        if (this is Failure) action(error)
        return this
    }

    // Chaining function for actual crashes/timeouts
    inline fun onException(action: (Throwable) -> Unit): ApiResult<T> {
        if (this is Exception) action(exception)
        return this
    }
}

data class ApiError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("error_code")
    val errorCode: String,
    @SerializedName("msg")
    val message: String
)

/**
```
2026-02-17 17:20:45.397 17156-18526 okhttp.OkHttpClient     com.lokahe.androidmvvm               I
{
"access_token":"eyJhbGciOiJFUzI1NiIsImtpZCI6IjhkOTlkNGFjLTFjM2MtNGQ0YS1iYjljLTI1Y2VjYTA4YWE3NyIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2tvbGllaHZuZHJpcXN2cml2aW9vLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiIyYjNjYmE4Zi00MjBmLTRkOGMtODdhOC1jNjY2MDc1MTI2ODciLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzcxMzIwMDQzLCJpYXQiOjE3NzEzMTY0NDMsImVtYWlsIjoibG9rYWhlNjIwQGdtYWlsLmNvbSIsInBob25lIjoiIiwiYXBwX21ldGFkYXRhIjp7InByb3ZpZGVyIjoiZW1haWwiLCJwcm92aWRlcnMiOlsiZW1haWwiXX0sInVzZXJfbWV0YWRhdGEiOnsiZW1haWwiOiJsb2thaGU2MjBAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBob25lX3ZlcmlmaWVkIjpmYWxzZSwic3ViIjoiMmIzY2JhOGYtNDIwZi00ZDhjLTg3YTgtYzY2NjA3NTEyNjg3In0sInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiYWFsIjoiYWFsMSIsImFtciI6W3sibWV0aG9kIjoib3RwIiwidGltZXN0YW1wIjoxNzcxMzE2NDQzfV0sInNlc3Npb25faWQiOiJjYzlhNjg3MC04MjZhLTRjMDktYmM2MC04Y2RhMzBjZjNlZWIiLCJpc19hbm9ueW1vdXMiOmZhbHNlfQ.JT9W6etQBFKxgGTypsoemNILjHKbufvUjD1dJxpZubvVDyaZyt5u83tn_XvudBX6jPpILsAOgR4LCQXm1UQjYA",
"token_type":"bearer",
"expires_in":3600,
"expires_at":1771320043,
"refresh_token":"j5j6esejsex2",
"user":{"id":"2b3cba8f-420f-4d8c-87a8-c66607512687",
"aud":"authenticated",
"role":"authenticated",
"email":"lokahe620@gmail.com",
"email_confirmed_at":"2026-02-17T08:20:43.754955Z",
"phone":"",
"confirmation_sent_at":"2026-02-17T08:20:00.778843Z",
"confirmed_at":"2026-02-17T08:20:43.754955Z",
"last_sign_in_at":"2026-02-17T08:20:43.76476609Z",
"app_metadata":{"provider":"email",
"providers":["email"]},
"user_metadata":{"email":"lokahe620@gmail.com",
"email_verified":true,
"phone_verified":false,
"sub":"2b3cba8f-420f-4d8c-87a8-c66607512687"},
"identities":[{"identity_id":"85dcce80-420c-499a-b136-fb98f9aa5679",
"id":"2b3cba8f-420f-4d8c-87a8-c66607512687",
"user_id":"2b3cba8f-420f-4d8c-87a8-c66607512687",
"identity_data":{"email":"lokahe620@gmail.com",
"email_verified":true,
"phone_verified":false,
"sub":"2b3cba8f-420f-4d8c-87a8-c66607512687"},
"provider":"email",
"last_sign_in_at":"2026-02-17T07:36:16.456415Z",
"created_at":"2026-02-17T07:36:16.456468Z",
"updated_at":"2026-02-17T07:36:16.456468Z",
"email":"lokahe620@gmail.com"}],
"created_at":"2026-02-17T07:36:16.450927Z",
"updated_at":"2026-02-17T08:20:43.795635Z",
"is_anonymous":false}}
```
 */
data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("expires_at")
    val expiresAt: Long?,
    @SerializedName("user")
    val user: User?
)
