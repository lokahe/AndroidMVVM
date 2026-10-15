package com.lokahe.log

/**
 * 日志级别，按严重程度递增（对应 android.util.Log 的优先级）。
 *
 * 过滤规则：只有 [priority] 大于等于当前配置级别的日志才会输出。
 */
enum class LogLevel(val priority: Int) {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6);

    /** 判断目标级别是否允许输出（目标级别 >= 自身）。 */
    fun allows(level: LogLevel): Boolean = level >= this
}
