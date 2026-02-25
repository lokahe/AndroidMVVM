package com.lokahe.androidmvvm.data.models.supabase

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("birth")
    val birth: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("last_sign_in_at")
    val lastSignInAt: String,
    @SerializedName("email_confirmed_at")
    val emailConfirmedAt: String,
    // join
    @SerializedName("followers")
    var followers: List<Followers>,
    @SerializedName("following_list")
    var followingList: List<Follower>
)

data class Followers(
    @SerializedName("count")
    val count: Int
)

data class Follower(
    @SerializedName("target_id")
    val targetId: String
)