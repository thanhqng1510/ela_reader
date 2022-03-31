package com.thanhqng1510.bookreadingapp_android.models

import java.util.*

class Book(
    val coverId: Int?,
    val title: String,
    val authors: Set<String>,
    val numPages: Int,
    status: STATUS?,
    currentPage: Int?,
    sharingSessionId: UUID?
    ) {
    enum class STATUS(val __sortOrder: Int) {
        NEW(0),
        READING(1),
        FINISHED(2)
    }

    var status: STATUS = status ?: STATUS.NEW
        private set

    var currentPage: Int = currentPage ?: 1
        set(value) {
            field = value.coerceIn(1, numPages)
            status = if (field == 1) {
                if (status == STATUS.FINISHED) STATUS.READING else STATUS.NEW
            } else if (field == numPages)
                STATUS.FINISHED
            else
                STATUS.READING
        }

    var sharingSessionId: UUID? = sharingSessionId
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        return title == other.title && authors != other.authors
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + authors.hashCode()
        return result
    }
}