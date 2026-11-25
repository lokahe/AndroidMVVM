package com.lokahe.domain.model

/**
 * Outcome of the unified email-entry request (POST /api/auth/email-entry):
 * - [LoggedIn]: credentials were valid and the session is ready.
 * - [CodeSent]: a verification email was sent (unknown account, wrong
 *   password, or passwordless entry); the code step decides between
 *   logging in and creating the account.
 */
sealed interface AuthEntryOutcome {
    data class LoggedIn(val auth: AuthResult) : AuthEntryOutcome
    data object CodeSent : AuthEntryOutcome
}
