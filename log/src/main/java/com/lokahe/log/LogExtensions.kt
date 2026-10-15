package com.lokahe.log

/**
 * 以调用方类名为 Tag 的便捷扩展。
 *
 * 惰性求值：级别被过滤时不会构建字符串。
 *
 * 示例：
 * ```
 * class MainViewModel {
 *     fun load() {
 *         logD { "loading posts" }
 *         logE(throwable) { "load failed" }
 *     }
 * }
 * ```
 */
fun Any.logV(message: () -> String) {
    if (L.isLoggable(LogLevel.VERBOSE)) L.v(javaClass.simpleName, message())
}

fun Any.logD(message: () -> String) {
    if (L.isLoggable(LogLevel.DEBUG)) L.d(javaClass.simpleName, message())
}

fun Any.logI(message: () -> String) {
    if (L.isLoggable(LogLevel.INFO)) L.i(javaClass.simpleName, message())
}

fun Any.logW(message: () -> String) {
    if (L.isLoggable(LogLevel.WARN)) L.w(javaClass.simpleName, message())
}

fun Any.logE(throwable: Throwable? = null, message: () -> String) {
    if (L.isLoggable(LogLevel.ERROR)) L.e(javaClass.simpleName, message(), throwable)
}

