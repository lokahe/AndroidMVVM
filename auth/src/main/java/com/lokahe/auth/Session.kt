package com.lokahe.auth

import kotlinx.serialization.Serializable

/**
 * Backend-neutral session model.
 *
 * Holds only the credential material needed to talk to any backend:
 * the access/refresh tokens, their expiry (epoch seconds) and the user id.
 * Provider-specific profile data is intentionally NOT part of the session.
 */
@Serializable
data class Session(
    val userId: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
)