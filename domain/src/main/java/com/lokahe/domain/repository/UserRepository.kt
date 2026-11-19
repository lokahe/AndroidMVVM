package com.lokahe.domain.repository

import com.lokahe.domain.model.Follower
import com.lokahe.domain.model.Following
import com.lokahe.domain.model.Profile
import com.lokahe.http.ApiResult
import kotlinx.coroutines.flow.Flow

/** User/profile contract — implementation-agnostic. */
interface UserRepository {
    /** Fetch the signed-in user's own profile (rovortex: GET /api/users/me). */
    fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>>

    /** Update the signed-in user's avatar URL (rovortex: PATCH profile). */
    fun updateAvatar(token: String, url: String): Flow<ApiResult<Profile>>

    fun fetchProfilesByIds(token: String, ids: List<String>): Flow<ApiResult<List<Profile>>>

    fun fetchFollowingIds(token: String, followerId: String): Flow<ApiResult<List<Following>>>

    fun fetchFollowerIds(token: String, targetId: String): Flow<ApiResult<List<Follower>>>

    fun follow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>>

    fun unFollow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>>
}