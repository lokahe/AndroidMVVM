package com.lokahe.log

import java.util.concurrent.CopyOnWriteArrayList

/**
 * 统一日志门面 —— 其他模块的唯一日志入口。
 *
 * 特性：
 * - 对内管理日志级别：全局 [LogConfig.minLevel] + 每个 Sink 自身的 minLevel 双层过滤
 * - 输出端可插拔：默认 Logcat，后续可接入 MMKV 存储 / 崩溃上报（实现 [LogSink] 即可）
 * - 未初始化也可安全调用（默认输出 Logcat、DEBUG 级别）
 * - 日志系统自身异常被吞掉，绝不影响业务
 *
 * 用法：
 * ```
 * // Application.onCreate 中初始化
 * L.init(LogConfig(minLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN))
 *
 * // 业务代码（推荐惰性写法，级别不通过时不产生字符串拼接开销）
 * L.d(TAG) { "message $value" }
 * L.e(TAG, throwable) { "load failed" }
 *
 * // 或以当前类名为 Tag 的扩展函数
 * logD { "message" }
 * ```
 */
object L {

    @Volatile
    private var config: LogConfig = LogConfig()

    private val sinks = CopyOnWriteArrayList<LogSink>().apply { add(LogcatSink()) }

    /** 初始化（在 Application.onCreate 中调用）；重复调用会覆盖上一次配置。 */
    fun init(config: LogConfig) {
        this.config = config
        sinks.clear()
        if (config.sinks.isEmpty()) sinks.add(LogcatSink()) else sinks.addAll(config.sinks)
    }

    /** 运行期动态调整全局级别（无需重新 init）。 */
    fun setLevel(level: LogLevel) {
        config = config.copy(minLevel = level)
    }

    /** 当前全局级别。 */
    fun currentLevel(): LogLevel = config.minLevel

    /** 指定级别当前是否可输出。 */
    fun isLoggable(level: LogLevel): Boolean = config.enabled && config.minLevel.allows(level)

    // ---- 立即求值版本 ----

    fun v(tag: String, message: String, throwable: Throwable? = null) =
        dispatch(LogLevel.VERBOSE, tag, message, throwable)

    fun d(tag: String, message: String, throwable: Throwable? = null) =
        dispatch(LogLevel.DEBUG, tag, message, throwable)

    fun i(tag: String, message: String, throwable: Throwable? = null) =
        dispatch(LogLevel.INFO, tag, message, throwable)

    fun w(tag: String, message: String, throwable: Throwable? = null) =
        dispatch(LogLevel.WARN, tag, message, throwable)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        dispatch(LogLevel.ERROR, tag, message, throwable)

    // ---- 惰性求值版本：级别被过滤时不构建字符串 ----

    fun v(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isLoggable(LogLevel.VERBOSE)) dispatch(LogLevel.VERBOSE, tag, message(), throwable)
    }

    fun d(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isLoggable(LogLevel.DEBUG)) dispatch(LogLevel.DEBUG, tag, message(), throwable)
    }

    fun i(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isLoggable(LogLevel.INFO)) dispatch(LogLevel.INFO, tag, message(), throwable)
    }

    fun w(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isLoggable(LogLevel.WARN)) dispatch(LogLevel.WARN, tag, message(), throwable)
    }

    fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
        if (isLoggable(LogLevel.ERROR)) dispatch(LogLevel.ERROR, tag, message(), throwable)
    }

    private fun dispatch(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (!isLoggable(level)) return
        for (sink in sinks) {
            if (sink.minLevel.allows(level)) {
                try {
                    sink.log(level, tag, message, throwable)
                } catch (_: Exception) {
                    // 日志输出端异常不能影响业务
                }
            }
        }
    }
}
