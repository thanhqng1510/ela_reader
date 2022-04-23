package com.thanhqng1510.bookreadingapp_android.utils.coroutine_utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CoroutineUtils {
    fun CoroutineScope.retry(
        delayMillis: Long,
        maxRepeat: Int,
        block: suspend CoroutineScope.() -> Boolean
    ) = launch {
        var repeatTime = 0
        while (!block() && repeatTime <= maxRepeat) {
            delay(delayMillis)
            ++repeatTime
        }
    }
}