package com.lokahe.backend.rovortex.mapper

import com.lokahe.auth.Session
import com.lokahe.backend.rovortex.model.AuthResponse
import com.lokahe.backend.rovortex.model.MeResponse
import com.lokahe.backend.rovortex.model.RovortexUser
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.User

/**
 * DTO → domain mapping. Rovortex has no profile/follow system yet, so the
 * domain user carries the account fields and profile stays null.
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
    fullName = username,
    avatarUrl = null,
    profile = null
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
