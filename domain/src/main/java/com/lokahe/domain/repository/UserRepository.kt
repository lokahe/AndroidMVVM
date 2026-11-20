package com.lokahe.domain.repository

import com.lokahe.domain.model.IdentityInfo
import com.lokahe.domain.model.Profile
import com.lokahe.domain.model.SessionInfo
import com.lokahe.http.ApiResult
import kotlinx.coroutines.flow.Flow

/** User/profile contract — implementation-agnostic. */
interface UserRepository {
    /** Fetch the signed-in user's own profile (rovortex: GET /api/users/me). */
    fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>>

    /** Update the signed-in user's avatar URL (rovortex: PATCH profile). */
    fun updateAvatar(token: String, url: String): Flow<ApiResult<Profile>>

    /** Connected OAuth/magic platforms (rovortex: GET /api/users/me/identities). */
    fun fetchIdentities(token: String): Flow<ApiResult<List<IdentityInfo>>>

    /** Active login devices/sessions (rovortex: GET /api/users/me/sessions). */
    fun fetchSessions(token: String): Flow<ApiResult<List<SessionInfo>>>

    /** Revoke one device session (rovortex: DELETE /api/users/me/sessions/{id}). */
    fun kickSession(token: String, id: String): Flow<ApiResult<Unit>>
}