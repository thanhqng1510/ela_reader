package com.thanhqng1510.bookreadingapp_android.activities.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.MessageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil
) : ViewModel() {
    lateinit var bookData: Book

    fun getBookByIdAsync(id: Long) = viewModelScope.async {
        return@async dataStore.getBookByIdAsync(id).await()?.let {
            bookData = it
            return@let ""
        } ?: run {
            logUtil.error("Failed to fetch book with id: $id", true)
            return@run MessageUtils.bookFetchFailedFriendly
        }
    }

    // Do not launch coroutine here as this method is call in onStop -> coroutine will be cancel
    // Update with database lifecycle scope instead
    fun closeBook() {
        bookData.lastRead = LocalDateTime.now()
        dataStore.updateBookAsync(bookData)
    }
}