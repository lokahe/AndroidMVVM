package com.lokahe.backend.rovortex

import android.net.Uri
import com.lokahe.backend.rovortex.mapper.toAuthResult
import com.lokahe.backend.rovortex.mapper.toDomain
import com.lokahe.backend.rovortex.model.AuthRequest
import com.lokahe.backend.rovortex.model.RefreshRequest
import com.lokahe.backend.rovortex.model.RegisterRequest
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.Follower
import com.lokahe.domain.model.Following
import com.lokahe.domain.model.Post
import com.lokahe.domain.model.PostRequest
import com.lokahe.domain.model.Profile
import com.lokahe.domain.model.User
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.PostRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.http.ApiError
import com.lokahe.http.ApiResult
import com.lokahe.http.safeApiCall
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Rovortex implementation of the domain repository contracts.
 *
 * Auth (password + GitHub OAuth) is fully wired to the gateway API. Posts,
 * profiles, follows and uploads are not implemented server-side yet, so
 * [PostRepository] and most of [UserRepository] are stubs that fail with a
 * 501-style error instead of hitting the network.
 */
class RovortexRepository @Inject constructor(
    private val apiService: ApiService
) : AuthRepository, UserRepository, PostRepository {

    /** Map the payload of a successful result while passing through Loading/Failure/Exception. */
    private inline fun <T, R> Flow<ApiResult<T>>.asDomain(crossinline transform: (T) -> R): Flow<ApiResult<R>> =
        map { result ->
            when (result) {
                is ApiResult.Success -> ApiResult.Success(transform(result.data))
                is ApiResult.Loading -> result
                is ApiResult.Failure -> result
                is ApiResult.Exception -> result
            }
        }

    private fun <T> notImplemented(): Flow<ApiResult<T>> = flow {
        emit(ApiResult.Failure(ApiError(501, "not_implemented", "Not supported by rovortex backend yet")))
    }

    // ---- AuthRepository ----

    override fun signInWithPassword(login: String, password: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.login(AuthRequest(login, password)) }
            .asDomain { it.toAuthResult() }

    override fun signUp(username: String, email: String, password: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.register(RegisterRequest(username, email, password)) }
            .asDomain { it.toAuthResult() }

    override fun verifyToken(token: String): Flow<ApiResult<User>> =
        safeApiCall { apiService.me(bearerToken = token.bearer) }
            .asDomain { it.toDomain() }

    override fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.refreshToken(RefreshRequest(refreshToken)) }
            .asDomain { it.toAuthResult() }

    override fun signOut(token: String, refreshToken: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.logout(RefreshRequest(refreshToken)) }

    override fun buildAuthorizeUrl(provider: String): String = Api.authorizeUrl(provider)

    /**
     * Parse the JSON body the gateway renders at the OAuth callback URL:
     * an AuthResponse on success, {"error":{...}} on failure.
     */
    override fun parseOAuthResponse(body: String): AuthResult? =
        GsonProvider.authResponse(body)?.takeIf { !it.token.isNullOrEmpty() }?.toAuthResult()

    // ---- UserRepository (profile/follow not available on rovortex yet) ----

    override fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>> =
        notImplemented()

    override fun fetchProfilesByIds(token: String, ids: List<String>): Flow<ApiResult<List<Profile>>> =
        notImplemented()

    override fun fetchFollowingIds(token: String, followerId: String): Flow<ApiResult<List<Following>>> =
        notImplemented()

    override fun fetchFollowerIds(token: String, targetId: String): Flow<ApiResult<List<Follower>>> =
        notImplemented()

    override fun follow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>> =
        notImplemented()

    override fun unFollow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>> =
        notImplemented()

    // ---- PostRepository (posts not available on rovortex yet) ----

    override fun insertPost(token: String, post: PostRequest): Flow<ApiResult<List<Post>>> =
        notImplemented()

    override fun fetchPosts(
        token: String,
        authorId: String?,
        userId: String?,
        replyId: String,
        limit: Int,
        offset: Int
    ): Flow<ApiResult<List<Post>>> = notImplemented()

    override fun deletePosts(token: String, ids: List<String>): Flow<ApiResult<Any>> =
        notImplemented()

    override fun like(token: String, postId: String, userId: String): Flow<ApiResult<Unit>> =
        notImplemented()

    override fun dislike(token: String, postId: String, userId: String): Flow<ApiResult<Unit>> =
        notImplemented()

    override fun uploadImage(
        token: String,
        path: String,
        imageUri: Uri,
        onProgress: (percent: Int) -> Unit
    ): Flow<ApiResult<Any>> = notImplemented()
}

private val String.bearer: String get() = "Bearer $this"
