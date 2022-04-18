package com.thanhqng1510.bookreadingapp_android.activities.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.MessageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil
) : ViewModel() {
    var bookData: Book? = null

    fun getBookByIdAsync(id: Long) = viewModelScope.async {
        bookData = dataStore.getBookByIdAsync(id).await()
        if (bookData == null) {
            logUtil.error("Failed to fetch book with id: $id", true)
            return@async MessageUtils.bookFetchFailedFriendly
        }
        return@async null
    }
}