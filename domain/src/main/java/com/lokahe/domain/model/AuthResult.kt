package com.lokahe.domain.model

import com.lokahe.auth.Session

/**
 * The result of an authentication grant: the neutral session credentials
 * plus the user the backend returned for this grant (if any). Kept as one
 * value so callers can persist both without an extra round trip.
 */
data class AuthResult(
    val session: Session,
    val user: User?
)