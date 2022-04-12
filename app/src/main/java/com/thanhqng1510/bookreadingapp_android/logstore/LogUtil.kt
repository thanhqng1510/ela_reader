package com.thanhqng1510.bookreadingapp_android.logstore

import com.thanhqng1510.bookreadingapp_android.models.entities.logentry.LogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class LogUtil @Inject constructor(
    private val logStore: LogStore,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    enum class LOGLEVEL(val tag: String) {
        INFO("Info"),
        WARN("Warn"),
        CRITICAL("Critical"),
        ERROR("Error");

        companion object {
            fun forTag(tag: String): LOGLEVEL = values().find { it.tag == tag }!!
        }
    }

    fun info(message: String) = log(LOGLEVEL.INFO, message, false)

    fun warn(message: String, includeStackTrace: Boolean) =
        log(LOGLEVEL.WARN, message, includeStackTrace)

    fun critical(message: String, includeStackTrace: Boolean) =
        log(LOGLEVEL.CRITICAL, message, includeStackTrace)

    fun error(message: String, includeStackTrace: Boolean) =
        log(LOGLEVEL.ERROR, message, includeStackTrace)

    private fun log(level: LOGLEVEL, message: String, includeThreadInfo: Boolean) {
        scope.launch {
            logStore.logEntryDao().insert(
                LogEntry(
                    level, LocalDateTime.now(),
                    Thread.currentThread().id, Thread.currentThread().name, message,
                    if (includeThreadInfo) Thread.currentThread().stackTrace.contentDeepToString() else null
                )
            )
        }
    }
}