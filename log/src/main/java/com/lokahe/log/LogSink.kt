package com.lokahe.log

import android.util.Log

/**
 * 日志输出端（Sink）。:log 模块内置 [LogcatSink]；
 * 后续接入 MMKV 持久化、崩溃上报时，实现本接口并在 [LogConfig.sinks] 中注册即可，
 * 无需改动任何调用方代码。
 */
interface LogSink {

    /** 该 Sink 自身的最小输出级别，可与全局级别不同（如文件端只落 ERROR）。 */
    val minLevel: LogLevel get() = LogLevel.VERBOSE

    /**
     * 输出一条日志。仅当级别通过全局与 Sink 自身过滤后才会被调用。
     *
     * @param level 日志级别
     * @param tag 标签（一般为调用方类名）
     * @param message 日志内容
     * @param throwable 可选异常（可为 null）
     */
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?)
}

/** 默认输出端：写入 Logcat。 */
class LogcatSink(override val minLevel: LogLevel = LogLevel.VERBOSE) : LogSink {

    override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
        }
    }
}
