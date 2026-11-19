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
    /** Sign in / register with email (or username) + password. */
    fun signInWithPassword(login: String, password: String): Flow<ApiResult<AuthResult>>

    /** Create an account with username + email + password; returns a session. */
    fun signUp(username: String, email: String, password: String): Flow<ApiResult<AuthResult>>

    /** Validate the current access token and fetch the signed-in user. */
    fun verifyToken(token: String): Flow<ApiResult<User>>

    /** Rotate the session using the refresh token. */
    fun refreshToken(refreshToken: String): Flow<ApiResult<AuthResult>>

    /** Revoke the session server-side. */
    fun signOut(token: String, refreshToken: String): Flow<ApiResult<Any>>

    /**
     * Ask the backend to email a password-reset link for [email].
     * The response is always accepted (anti-enumeration); the actual reset
     * happens in the browser via the emailed link.
     */
    fun requestPasswordReset(email: String): Flow<ApiResult<Any>>

    /**
     * Start the browser-based OAuth flow for [provider] (e.g. "github").
     * The caller presents the URL in a web view / browser and feeds the
     * callback payload back via [parseOAuthResponse].
     */
    fun buildAuthorizeUrl(provider: String): String

    /**
     * Parse the OAuth callback payload (the JSON the backend renders at its
     * callback URL) into an [AuthResult]; null when it is not a session.
     */
    fun parseOAuthResponse(body: String): AuthResult?
}