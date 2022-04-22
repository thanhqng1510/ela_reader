package com.thanhqng1510.bookreadingapp_android.activities.reader

import android.app.Application
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.MessageUtils
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

    // null => not currently playing
    // not null => audio is playing
    private var ambientPlayer: SoundPool? = null

    override fun onCleared() {
        stopAmbientSound()
        super.onCleared()
    }

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

    fun playAmbientSoundAsync() {
        if (ambientPlayer != null)
            return

        viewModelScope.launch {
            dataStore.getSelectedAmbientSoundAsync(getApplication()).await()?.let { ambientSound ->
                ambientPlayer = SoundPool.Builder().setMaxStreams(1).build()

                ambientPlayer?.setOnLoadCompleteListener { player, sampleId, _ ->
                    player.play(sampleId, 1.0f, 1.0f, 1, -1, 1.0f)
                }

                ambientPlayer?.load(
                    getApplication(),
                    ambientSound.resId,
                    1
                )
            }
        }
    }

    fun stopAmbientSound() {
        ambientPlayer?.release()
        ambientPlayer = null
    }
}