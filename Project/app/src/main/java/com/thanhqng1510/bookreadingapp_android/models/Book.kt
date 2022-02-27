package com.thanhqng1510.bookreadingapp_android.models

import java.util.*

data class Book(
    val coverId: Int?,
    val title: String,
    val authors: Array<String>,
    val pages: Int,
    var sharingSessionId: UUID?
) {
    enum class STATUS(val sortOrder: Int) {
        READING(0),
        NEW(1),
        FINISHED(2)
    }

    var status: STATUS = STATUS.NEW
        private set

    var currentPage: Int = 1
        set(value) {
            field = value

            status = if (field <= 1) {
                if (status == STATUS.FINISHED)
                    STATUS.READING
                else
                    STATUS.NEW
            } else if (field == pages)
                STATUS.FINISHED
            else
                STATUS.READING
        }
}