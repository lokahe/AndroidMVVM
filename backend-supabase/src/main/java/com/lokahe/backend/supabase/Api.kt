package com.lokahe.backend.supabase

import com.lokahe.backend.supabase.BuildConfig

object Api {
    const val SPB_URL = BuildConfig.SPB_URL
    const val ANON_KEY = BuildConfig.ANON_KEY

    const val REDIRECT = "lokahe://androidmvvm"
    const val SPB_AUTH_URL = "${SPB_URL}auth/v1/authorize"

    /** PostgREST id-exclusion sentinel meaning "any real id". */
    const val EMPTY_UUID = "00000000-0000-0000-0000-000000000000"
}

// PostgREST query helpers — Supabase-specific, owned by this module only
val String.b: String get() = "Bearer $this"
val String.eq: String get() = "eq.$this"
val String.neq: String get() = "neq.$this"
val String.ins: String get() = "in.($this)"