package com.lokahe.backend.supabase.model

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("birth")
    val birth: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("last_sign_in_at")
    val lastSignInAt: String?,
    @SerializedName("email_confirmed_at")
    val emailConfirmedAt: String?,
    // join
    @SerializedName("followers")
    val followers: List<Followers>?,
    @SerializedName("followings")
    val followings: List<Followers>?,
    @SerializedName("following_list")
    val followingList: List<Following>?
)

data class Followers(
    @SerializedName("count")
    val count: Int
)

data class Following(
    @SerializedName("target_id")
    val targetId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Following) return false
        return targetId == other.targetId
    }

    override fun hashCode(): Int {
        return targetId.hashCode()
    }
}

data class Follower(
    @SerializedName("follower_id")
    val followerId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Follower) return false
        return followerId == other.followerId
    }

    override fun hashCode(): Int {
        return followerId.hashCode()
    }
}