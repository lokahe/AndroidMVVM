package com.lokahe.backend.rovortex

import com.lokahe.backend.rovortex.mapper.buildProfilePatch
import com.lokahe.backend.rovortex.mapper.toAuthResult
import com.lokahe.backend.rovortex.mapper.toDomain
import com.lokahe.backend.rovortex.model.AuthRequest
import com.lokahe.backend.rovortex.model.EmailEntryConfirmRequest
import com.lokahe.backend.rovortex.model.EmailEntryRequest
import com.lokahe.backend.rovortex.model.PasswordResetRequest
import com.lokahe.backend.rovortex.model.RefreshRequest
import com.lokahe.domain.model.AuthEntryOutcome
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.IdentityInfo
import com.lokahe.domain.model.Profile
import com.lokahe.domain.model.SessionInfo
import com.lokahe.domain.model.User
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.http.ApiError
import com.lokahe.http.ApiResult
import com.lokahe.http.parseError
import com.lokahe.http.safeApiCall
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Rovortex implementation of the domain repository contracts.
 *
 * Auth (password + GitHub OAuth) and the signed-in user surface (profile,
 * identities, sessions) are fully wired to the gateway API.
 */
class RovortexRepository @Inject constructor(
    private val apiService: ApiService
) : AuthRepository, UserRepository {

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

    /**
     * Unified email entry: 200 carries AuthResponse (direct login); 202 has an
     * empty body (verification email sent). The endpoint is typed as raw
     * ResponseBody because the Gson converter throws on the empty 202 body —
     * the JSON is decoded manually only on 200.
     */
    override fun emailEntry(email: String, password: String): Flow<ApiResult<AuthEntryOutcome>> =
        flow {
            emit(ApiResult.Loading)
            val result: ApiResult<AuthEntryOutcome> = runCatching {
                apiService.emailEntry(EmailEntryRequest(email.trim(), password))
            }.fold(
                { response ->
                    when {
                        response.code() == 200 -> {
                            val json = response.body()?.string()
                            val auth = json?.let { GsonProvider.authResponse(it) }
                            if (auth != null) {
                                ApiResult.Success(AuthEntryOutcome.LoggedIn(auth.toAuthResult()))
                            } else {
                                ApiResult.Failure(parseErrorResponse(200, json))
                            }
                        }

                        response.isSuccessful -> ApiResult.Success(AuthEntryOutcome.CodeSent)
                        else -> ApiResult.Failure(
                            parseErrorResponse(response.code(), response.errorBody()?.string())
                        )
                    }
                },
                { e -> ApiResult.Exception(e) }
            )
            emit(result)
        }

    override fun emailEntryConfirm(
        email: String,
        code: String,
        password: String?
    ): Flow<ApiResult<AuthResult>> =
        safeApiCall {
            apiService.emailEntryConfirm(EmailEntryConfirmRequest(email.trim(), code.trim(), password))
        }.asDomain { it.toAuthResult() }

    override fun signInWithPassword(login: String, password: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.login(AuthRequest(login, password)) }
            .asDomain { it.toAuthResult() }

    override fun verifyToken(token: String): Flow<ApiResult<User>> =
        safeApiCall { apiService.me(bearerToken = token.bearer) }
            .asDomain { it.toDomain() }

    override fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResult>> =
        safeApiCall { apiService.refreshToken(RefreshRequest(refreshToken)) }
            .asDomain { it.toAuthResult() }

    override fun signOut(token: String, refreshToken: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.logout(RefreshRequest(refreshToken)) }

    override fun requestPasswordReset(email: String): Flow<ApiResult<Any>> =
        safeApiCall { apiService.requestPasswordReset(PasswordResetRequest(email.trim())) }

    override fun buildAuthorizeUrl(provider: String): String = Api.authorizeUrl(provider)

    /**
     * Parse the JSON body the gateway renders at the OAuth callback URL:
     * an AuthResponse on success, {"error":{...}} on failure.
     */
    override fun parseOAuthResponse(body: String): AuthResult? =
        GsonProvider.authResponse(body)?.takeIf { !it.token.isNullOrEmpty() }?.toAuthResult()

    // ---- UserRepository ----

    override fun fetchProfileById(token: String, id: String): Flow<ApiResult<Profile>> =
        safeApiCall { apiService.me(bearerToken = token.bearer) }
            .asDomain { it.toDomain().profile!! }

    override fun updateAvatar(token: String, url: String): Flow<ApiResult<Profile>> =
        safeApiCall {
            apiService.patchProfile(
                bearerToken = token.bearer,
                body = buildProfilePatch(avatarUrl = url, hasAvatarUrl = true)
            )
        }.asDomain { it.toDomain().profile!! }

    override fun fetchIdentities(token: String): Flow<ApiResult<List<IdentityInfo>>> =
        safeApiCall { apiService.identities(bearerToken = token.bearer) }
            .asDomain { list ->
                list.map {
                    IdentityInfo(
                        provider = it.provider,
                        email = it.email,
                        avatarUrl = it.avatarUrl,
                        createdAt = it.createdAt
                    )
                }
            }

    override fun fetchSessions(token: String): Flow<ApiResult<List<SessionInfo>>> =
        safeApiCall { apiService.sessions(bearerToken = token.bearer) }
            .asDomain { list ->
                list.map {
                    SessionInfo(
                        id = it.id,
                        deviceLabel = it.deviceLabel,
                        userAgent = it.ua,
                        ip = it.ip,
                        createdAt = it.createdAt,
                        expiresAt = it.expiresAt
                    )
                }
            }

    override fun kickSession(token: String, id: String): Flow<ApiResult<Unit>> =
        safeApiCall { apiService.kickSession(bearerToken = token.bearer, id = id) }
}

private val String.bearer: String get() = "Bearer $this"

private fun parseErrorResponse(httpCode: Int, errorBody: String?): ApiError =
    parseError(httpCode, errorBody)
