package com.lokahe.domain.model

import com.lokahe.domain.Const
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String?,
    val name: String?,
    val avatar: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val birth: String?,
    val description: String?,
    val gender: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val lastSignInAt: String?,
    val emailConfirmedAt: String?,
    // join
    val followers: List<Followers>?,
    val followings: List<Followers>?,
    val followingList: List<Following>?
)

@Serializable
data class Followers(val count: Int)

@Serializable
data class Following(val targetId: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Following) return false
        return targetId == other.targetId
    }

    override fun hashCode(): Int {
        return targetId.hashCode()
    }
}

@Serializable
data class Follower(val followerId: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Follower) return false
        return followerId == other.followerId
    }

    override fun hashCode(): Int {
        return followerId.hashCode()
    }
}

/** Map a public profile to the uniform domain [User] shape. */
fun Profile.toUser(): User = User(
    id = this.id ?: Const.EMPTY_UUID,
    email = this.email,
    phone = this.phone ?: "",
    name = this.name,
    fullName = this.name,
    avatarUrl = this.avatar,
    profile = this
)