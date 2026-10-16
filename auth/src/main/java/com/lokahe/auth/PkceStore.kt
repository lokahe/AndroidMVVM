package com.lokahe.auth

import kotlinx.coroutines.flow.Flow

/**
 * Storage contract for the OAuth PKCE code_verifier
 * (generated before starting a browser authorize flow, consumed on redirect).
 */
interface PkceStore {
    val codeVerifierFlow: Flow<String?>

    /** Current code verifier snapshot (one-shot). */
    suspend fun current(): String?

    suspend fun save(codeVerifier: String)

    suspend fun clear()
}