package com.lokahe.androidmvvm.data.remote

import com.lokahe.androidmvvm.BuildConfig

object Api {
    const val GOOGLE_WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID
    const val SPB_URL = BuildConfig.SPB_URL
    const val ANON_KEY = BuildConfig.ANON_KEY

    const val REDIRECT = "lokahe://androidmvvm"
    const val SPB_AUTH_URL = "${SPB_URL}auth/v1/authorize"
    const val EMPTY_UUID = "00000000-0000-0000-0000-000000000000"
    const val PAGE_SIZE = 10
}

val String.b: String get() = "Bearer $this"
val String.eq: String get() = "eq.$this"
val String.neq: String get() = "neq.$this"
val String.ins: String get() = "in.($this)"