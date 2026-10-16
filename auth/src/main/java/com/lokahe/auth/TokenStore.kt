package com.lokahe.auth

import kotlinx.coroutines.flow.Flow

/**
 * Storage contract for session credentials.
 *
 * Implementations persist tokens independently of any backend SDK so the app
 * survives backend swaps without touching the UI/ViewModel layer.
 */
interface TokenStore {
    /** Stream of the persisted session; emits null when no session exists. */
    val sessionFlow: Flow<Session?>

    /** Current session snapshot (one-shot). */
    suspend fun current(): Session?

    /**
     * Persist a session. Non-null fields are merged into the stored session
     * (partial updates allowed, mirroring token-refresh semantics).
     */
    suspend fun save(session: Session)

    /** Remove all stored credentials. */
    suspend fun clear()
}