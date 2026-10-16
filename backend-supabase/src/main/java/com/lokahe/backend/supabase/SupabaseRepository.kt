package com.lokahe.backend.supabase

import android.content.Context
import android.net.Uri
import com.lokahe.backend.supabase.mapper.toAuthResult
import com.lokahe.backend.supabase.mapper.toDomain
import com.lokahe.backend.supabase.mapper.toDto
import com.lokahe.backend.supabase.model.CodeExchangeRequest
import com.lokahe.backend.supabase.model.FollowRequest
import com.lokahe.backend.supabase.model.GoogleAuth
import com.lokahe.backend.supabase.model.LikeRequest
import com.lokahe.backend.supabase.model.OtpRequest
import com.lokahe.backend.supabase.model.PostRequest as DtoPostRequest
import com.lokahe.backend.supabase.model.RefreshTokenRequest
import com.lokahe.backend.supabase.model.VerifyRequest
import com.lokahe.domain.Const
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
import com.lokahe.http.ApiResult
import com.lokahe.http.ProgressUriRequestBody
import com.lokahe.http.safeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody

/**
 * Supabase implementation of the domain repository contracts.
 * All DTO→domain mapping happens here; callers only see domain models.
 *
 * Swapping backends means writing another class like this and re-binding the
 * interfaces in the backend DI module — nothing above this layer changes.
 */
class SupabaseRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
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

    // ---- AuthRepository ----

    override fun signInWithIdToken(idToken: String, provider: String, nonce: String?): Flow<ApiResult<AuthResult>> =
        safeApiCall {
            apiService.googleAuth(
                body = GoogleAuth(idToken = idToken, provider = provider, nonce = nonce)
            )
        }.asDomain { it.toAuthResult() }

    override fun exchangeCode(code: String, codeVerifier: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.codeExchange(body = CodeExchangeRequest(code, codeVerifier)) }
            .asDomain { it.toAuthResult() }

    override fun verifyToken(token: String): Flow<ApiResult<User>> =
        safeApiCall { apiService.varifyToken(token = token.b) }.asDomain { it.toDomain() }

    override fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.refreshToken(body = RefreshTokenRequest(refreshToken)) }
            .asDomain { it.toAuthResult() }

    override fun signOut(token: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.signOut(token = token.b) }

    override fun requestOtp(email: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.sign(body = OtpRequest(email)) }

    override fun verifyOtp(email: String, token: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.verifyEmail(body = VerifyRequest("email", email, token)) }
            .asDomain { it.toAuthResult() }

    override fun buildAuthorizeUrl(provider: String, codeChallenge: String?): String =
        buildString {
            append(Api.SPB_AUTH_URL)
            append("?provider=").append(provider)
            append("&redirect_to=").append(Api.REDIRECT)
            if (!codeChallenge.isNullOrEmpty()) {
                append("&code_challenge=").append(codeChallenge)
                append("&code_challenge_method=S256")
            }
        }

    // ---- UserRepository ----

    override fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>> =
        safeApiCall { apiService.fetchProfileById(token = token.b, id = id.eq) }
            .asDomain { it.toDomain() }

    override fun fetchProfilesByIds(token: String, ids: List<String>): Flow<ApiResult<List<Profile>>> =
        safeApiCall {
            apiService.fetchProfilesByIds(
                token = token.b,
                idFilter = ids.joinToString(",").ins
            )
        }.asDomain { list -> list.map { it.toDomain() } }

    override fun fetchFollowingIds(token: String, followerId: String): Flow<ApiResult<List<Following>>> =
        safeApiCall { apiService.fetchFollowingIds(token = token.b, followerId = followerId.eq) }
            .asDomain { list -> list.map { Following(it.targetId) } }

    override fun fetchFollowerIds(token: String, targetId: String): Flow<ApiResult<List<Follower>>> =
        safeApiCall { apiService.fetchFollowerIds(token = token.b, targetId = targetId.eq) }
            .asDomain { list -> list.map { Follower(it.followerId) } }

    override fun follow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>> =
        safeApiCall {
            apiService.follow(token = token.b, body = FollowRequest(followerId, targetId))
        }

    override fun unFollow(token: String, followerId: String, targetId: String): Flow<ApiResult<Unit>> =
        safeApiCall {
            apiService.unFollow(token = token.b, followerId = followerId.eq, targetId = targetId.eq)
        }

    // ---- PostRepository ----

    override fun insertPost(token: String, post: PostRequest): Flow<ApiResult<List<Post>>> =
        safeApiCall {
            apiService.insertPost(
                token = token.b,
                body = with(post) {
                    DtoPostRequest(authorId, content, imageUrls, videoUrls, hashtag, replyPostId)
                }
            )
        }.asDomain { list -> list.map { it.toDomain() } }

    override fun fetchPosts(
        token: String,
        authorId: String?,
        userId: String?,
        replyId: String,
        limit: Int,
        offset: Int
    ): Flow<ApiResult<List<Post>>> =
        safeApiCall {
            apiService.fetchPosts(
                token = token.b,
                authorId = authorId?.takeIf { it.isNotEmpty() }?.let { authorId.eq },
                myUserId = userId?.eq ?: Const.EMPTY_UUID.eq,
                replyId = replyId.eq,
                limit = limit,
                offset = offset
            )
        }.asDomain { list -> list.map { it.toDomain() } }

    override fun deletePosts(token: String, ids: List<String>): Flow<ApiResult<Any>> =
        safeApiCall {
            apiService.deletePosts(token = token.b, inCondition = ids.joinToString(",").ins)
        }

    override fun like(token: String, postId: String, userId: String): Flow<ApiResult<Unit>> =
        safeApiCall { apiService.like(token = token.b, body = LikeRequest(postId, userId)) }

    override fun dislike(token: String, postId: String, userId: String): Flow<ApiResult<Unit>> =
        safeApiCall { apiService.dislike(token = token.b, postId = postId.eq, userId = userId.eq) }

    override fun uploadImage(
        token: String,
        path: String,
        imageUri: Uri,
        onProgress: (percent: Int) -> Unit
    ): Flow<ApiResult<Any>> {
        val progressBody = ProgressUriRequestBody(
            context,
            imageUri,
            "image/jpeg"
        ) { percent -> onProgress(percent) }
        return safeApiCall {
            apiService.uploadImage(
                bearerToken = token.b,
                path = path,
                image = MultipartBody.Part.createFormData("file", "image.jpg", progressBody)
            )
        }
    }
}