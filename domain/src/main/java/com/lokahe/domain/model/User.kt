package com.lokahe.domain.model

/**
 * Backend-neutral signed-in user.
 *
 * The provider-specific metadata block is flattened into first-class fields
 * (name / fullName / avatarUrl); the joined public profile is kept under
 * [profile] (var: refreshed in place after fetching the profile).
 */
data class User(
    val id: String,
    val email: String?,
    val phone: String?,
    val name: String?,
    val fullName: String?,
    val avatarUrl: String?,
    // join
    var profile: Profile?
)

fun User.name(): String? = profile?.name ?: name
fun User.avatar(): String? = profile?.avatar ?: avatarUrl