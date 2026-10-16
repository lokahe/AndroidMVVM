package com.lokahe.domain.repository

import com.lokahe.auth.Session
import com.lokahe.domain.model.AuthResult
import com.lokahe.domain.model.User
import com.lokahe.http.ApiResult
import kotlinx.coroutines.flow.Flow

/**
 * Authentication contract. The UI/ViewModel layer only sees [Session] /
 * domain [User]; every backend detail (endpoints, grant shapes, URLs) lives
 * in the implementation module. Swapping backends = re-binding this interface.
 */
interface AuthRepository {
    /** Exchange a platform credential (e.g. an OIDC id_token) for a session. */
    fun signInWithIdToken(idToken: String, provider: String, nonce: String?): Flow<ApiResult<AuthResult>>

    /** OAuth/PKCE authorization-code exchange (web-based providers). */
    fun exchangeCode(code: String, codeVerifier: String): Flow<ApiResult<AuthResult>>

    /** Validate the current access token and fetch the signed-in user. */
    fun verifyToken(token: String): Flow<ApiResult<User>>

    /** Rotate the session using the refresh token. */
    fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResult>>

    /** Revoke the session server-side. */
    fun signOut(token: String): Flow<ApiResult<Any>>

    /** Send a sign-in OTP (magic link) to [email]. */
    fun requestOtp(email: String): Flow<ApiResult<Any>>

    /** Verify an OTP token received via email. */
    fun verifyOtp(email: String, token: String): Flow<ApiResult<AuthResult>>

    /**
     * Build the backend's web-authorize URL that starts a browser-based
     * OAuth flow (the app handles the redirect via deep link).
     * [codeChallenge] is appended when a PKCE flow is used.
     */
    fun buildAuthorizeUrl(provider: String, codeChallenge: String?): String
}