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
    val emailConfirmedAt: String?
)

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