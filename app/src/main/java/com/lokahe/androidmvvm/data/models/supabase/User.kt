package com.lokahe.androidmvvm.data.models.supabase

import com.google.gson.annotations.SerializedName

/**
```
{
{
"id": "uuid-string",
"aud": "authenticated",
"role": "authenticated",
"email": "user@example.com",
"email_confirmed_at": "2024-01-01T00:00:00Z",
"phone": "",
"last_sign_in_at": "2024-02-16T12:00:00Z",
"app_metadata": { "provider": "email", "providers": ["email"] },
"user_metadata": { "full_name": "John Doe" },
"identities": [...],
"created_at": "2024-01-01T00:00:00Z",
"updated_at": "2024-02-16T12:00:00Z"
}
}
 */
data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("user_metadata")
    val userMetadata: UserMetadata,
    // local additional
    @SerializedName("public_profile")
    var profile: Profile
)

data class UserMetadata(
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)