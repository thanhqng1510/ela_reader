package com.thanhqng1510.ela_reader.screens.reader

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.datastore.DataStore
import com.thanhqng1510.ela_reader.logstore.LogUtil
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.models.entities.bookmark.Bookmark
import com.thanhqng1510.ela_reader.services.AmbientSoundPlayerService
import com.thanhqng1510.ela_reader.services.AmbientSoundPlayerService.AmbientSoundType
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil,
    application: Application
) : AndroidViewModel(application) {
    var bookData: Book? = null

    val selectedAmbientSoundType = MutableStateFlow<AmbientSoundType?>(null)

    val bookmarksFlow = dataStore.getAllBookmarksAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var currentBookmarkIconId = R.drawable.bookmark_collection_light

    init {
        getSelectedAmbientSoundTypeAsync()

        viewModelScope.launch {
            selectedAmbientSoundType.collectLatest {
                playAmbientSound()
                updateSelectedAmbientSoundTypeAsync()
            }
        }
    }

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

    fun addBookmark() {
        bookData?.let {
            dataStore.insertBookmarkAsync(Bookmark(it.currentPage, it.id, LocalDateTime.now()))
        }
    }

    /**
     * Do not launch coroutine here as this method is call in onStop -> coroutine will be cancel immediately
     *
     * Update with database lifecycle scope instead
     */
    private fun closeBook() {
        bookData?.let {
            it.lastRead = LocalDateTime.now()
            dataStore.updateBookAsync(it)
        }
    }

    private fun playAmbientSound() {
        selectedAmbientSoundType.value?.let {
            stopAmbientSound()

            if (it == AmbientSoundType.NONE)
                return

            val service = Intent(getApplication(), AmbientSoundPlayerService::class.java)
            service.putExtra(AmbientSoundPlayerService.arrayRawResIdExtra, it.resIds)
            getApplication<Application>().startService(service)
        }
    }

    private fun stopAmbientSound() = getApplication<Application>().stopService(
        Intent(
            getApplication(),
            AmbientSoundPlayerService::class.java
        )
    )

    private fun getSelectedAmbientSoundTypeAsync() = viewModelScope.launch {
        selectedAmbientSoundType.value =
            dataStore.getSelectedAmbientSoundAsync(getApplication()).await()
    }

    private fun updateSelectedAmbientSoundTypeAsync() = selectedAmbientSoundType.value?.let {
        dataStore.setSelectedAmbientSoundAsync(getApplication(), it)
    }
}