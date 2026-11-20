package com.lokahe.backend.rovortex.mapper

import com.lokahe.auth.Session
import com.lokahe.backend.rovortex.model.AuthResponse
import com.lokahe.backend.rovortex.model.MeResponse
import com.lokahe.backend.rovortex.model.ProfilePatch
import com.lokahe.backend.rovortex.model.RovortexUser
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.Profile
import com.lokahe.domain.model.User

/**
 * DTO → domain mapping. Rovortex exposes the profile via /api/users/me
 * (display_name / avatar_url / bio / website_url); it is mapped onto the
 * neutral domain Profile so the UI keeps a single source.
 */

fun RovortexUser.toDomain(): User = User(
    id = id,
    email = email,
    phone = null,
    name = username,
    fullName = username,
    avatarUrl = null,
    profile = null
)

fun MeResponse.toDomain(): User = User(
    id = id,
    email = email,
    phone = null,
    name = username,
    fullName = displayName ?: username,
    avatarUrl = avatarUrl,
    profile = Profile(
        id = id,
        name = displayName,
        avatar = avatarUrl,
        email = email,
        phone = null,
        address = null,
        birth = null,
        description = bio,
        gender = null,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastSignInAt = null,
        emailConfirmedAt = emailVerified?.toString()
    )
)

/** Double-Option PATCH: only fields explicitly set (even to null) are sent. */
fun buildProfilePatch(
    displayName: String? = null,
    hasDisplayName: Boolean = false,
    avatarUrl: String? = null,
    hasAvatarUrl: Boolean = false,
    bio: String? = null,
    hasBio: Boolean = false
): ProfilePatch = ProfilePatch(
    displayName = if (hasDisplayName) displayName else null,
    avatarUrl = if (hasAvatarUrl) avatarUrl else null,
    bio = if (hasBio) bio else null,
    websiteUrl = null
)

fun AuthResponse.toAuthResult(): AuthResult = AuthResult(
    session = Session(
        userId = user?.id,
        accessToken = token ?: "",
        refreshToken = refreshToken,
        // Rovortex exposes refresh expiry as RFC3339; the access JWT itself is
        // 2h. Store the refresh expiry epoch so BaseViewModel keeps refreshing
        // the access token while the session is alive.
        expiresAt = refreshExpiresAt?.toEpochSeconds()
    ),
    user = user?.toDomain()
)

private fun String.toEpochSeconds(): Long? = runCatching {
    java.time.Instant.parse(this).epochSecond
}.getOrNull()
