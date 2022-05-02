package com.thanhqng1510.ela_reader.logstore

import com.thanhqng1510.ela_reader.models.entities.logentry.LogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogUtil @Inject constructor(
    private val logStore: LogStore,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    enum class LogLevel(val tag: String) {
        INFO("Info"),
        ERROR("Error");

        companion object {
            fun forTag(tag: String): LogLevel? = values().find { it.tag == tag }
        }
    }

    fun info(message: String) = log(LogLevel.INFO, message, false)

    fun error(message: String, includeStackTrace: Boolean) =
        log(LogLevel.ERROR, message, includeStackTrace)

    private fun log(level: LogLevel, message: String, includeThreadInfo: Boolean) = scope.launch {
        logStore.logEntryDao().insert(
            LogEntry(
                level, LocalDateTime.now(),
                Thread.currentThread().id, Thread.currentThread().name, message,
                if (includeThreadInfo) Thread.currentThread().stackTrace.contentDeepToString() else null
            )
        )
    }
}