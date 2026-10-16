package com.lokahe.auth

import android.content.Context

/**
 * Platform-neutral credential-provider SPI.
 *
 * A provider only obtains a raw, backend-agnostic credential (e.g. an OIDC
 * id_token); exchanging it for a [Session] is the backend's job
 * (AuthRepository.signInWithIdToken). Adding a new platform (Apple, WeChat...)
 * means adding one implementation plus one @Named binding in AuthProviderModule
 * — no app/ViewModel changes required.
 */
interface AuthProvider {
    /** Provider identifier as known by the backend (e.g. "google"). */
    val id: String

    /**
     * Launch the platform sign-in UI and return the raw credential,
     * or null when the user cancels or the flow fails.
     */
    suspend fun signIn(context: Context): String?
}