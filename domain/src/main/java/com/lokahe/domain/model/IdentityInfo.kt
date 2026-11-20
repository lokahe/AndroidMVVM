package com.lokahe.domain.model

/**
 * A platform (OAuth / magic link) connected to the account. [provider] is the
 * backend's raw identifier ("github", "magic", ...); the client maps it to a
 * display name / icon.
 */
data class IdentityInfo(
    val provider: String,
    val email: String?,
    val createdAt: String?
)

/** One active login device/session of the current user. */
data class SessionInfo(
    val id: String,
    val deviceLabel: String?,
    val userAgent: String?,
    val ip: String?,
    val createdAt: String?,
    val expiresAt: String?
)
