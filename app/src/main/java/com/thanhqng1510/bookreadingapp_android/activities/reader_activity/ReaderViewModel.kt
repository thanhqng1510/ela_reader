package com.thanhqng1510.bookreadingapp_android.activities.reader_activity

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.Bookmark
import com.thanhqng1510.bookreadingapp_android.services.AmbientSoundPlayerService
import com.thanhqng1510.bookreadingapp_android.utils.constant_utils.ConstantUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil,
    application: Application
) : AndroidViewModel(application) {
    lateinit var bookData: Book

    override fun onCleared() {
        stopAmbientSound()
        closeBook()
        super.onCleared()
    }

    fun getBookByIdAsync(id: Long) = viewModelScope.async {
        return@async dataStore.getBookByIdAsync(id).await()?.let {
            bookData = it
            return@let ""
        } ?: run {
            logUtil.error("Failed to fetch book with id: $id", true)
            return@run ConstantUtils.bookFetchFailedFriendly
        }
    }

    fun playAmbientSoundAsync() = viewModelScope.launch {
        dataStore.getSelectedAmbientSoundAsync(getApplication()).await().let { ambientSound ->
            if (ambientSound == AmbientSoundPlayerService.AmbientSoundType.NONE)
                return@let

            val service = Intent(getApplication(), AmbientSoundPlayerService::class.java)
            service.putExtra(AmbientSoundPlayerService.rawResIdExtra, ambientSound.resId)
            getApplication<Application>().startService(service)
        }
    }

    fun addBookmark() = dataStore.insertBookmarkAsync(
        Bookmark(
            bookData.currentPage,
            bookData.id,
            LocalDateTime.now()
        )
    )

    /**
     * Do not launch coroutine here as this method is call in onStop -> coroutine will be cancel immediately
     *
     * Update with database lifecycle scope instead
     */
    private fun closeBook() {
        bookData.lastRead = LocalDateTime.now()
        dataStore.updateBookAsync(bookData)
    }

    private fun stopAmbientSound() = getApplication<Application>().stopService(
        Intent(
            getApplication(),
            AmbientSoundPlayerService::class.java
        )
    )
}