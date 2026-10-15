package com.lokahe.log

/**
 * 日志配置，在初始化时传入 [L.init]。
 *
 * @param minLevel 全局最小输出级别，低于该级别的日志直接丢弃
 * @param enabled 日志总开关（例如 release 构建可整体关闭）
 * @param sinks 输出端列表；默认仅输出到 Logcat，后续可注册 MMKV 存储、崩溃上报等 Sink
 */
data class LogConfig(
    val minLevel: LogLevel = LogLevel.DEBUG,
    val enabled: Boolean = true,
    val sinks: List<LogSink> = listOf(LogcatSink()),
)
