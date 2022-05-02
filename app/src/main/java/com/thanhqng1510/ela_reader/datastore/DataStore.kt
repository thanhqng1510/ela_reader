package com.thanhqng1510.ela_reader.datastore

import android.content.Context
import android.net.Uri
import com.thanhqng1510.ela_reader.datastore.localstore.LocalStore
import com.thanhqng1510.ela_reader.datastore.networkstore.NetworkStore
import com.thanhqng1510.ela_reader.datastore.sharedprefhelper.SharedPrefHelper
import com.thanhqng1510.ela_reader.logstore.LogUtil
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.models.entities.bookmark.Bookmark
import com.thanhqng1510.ela_reader.services.AmbientSoundPlayerService
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    private val localStore: LocalStore,
    private val networkStore: NetworkStore,
    private val sharedPrefHelper: SharedPrefHelper,
    private val logUtil: LogUtil
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getAllBooksAsFlow() = localStore.bookDao().getAllAsFlow()

    fun getBookByIdAsync(id: Long) = scope.async {
        return@async localStore.bookDao().getById(id)
    }

    fun insertBookAsync(book: Book) = scope.launch {
        val bookId = book.let {
            it.updateCurrentStatus()
            localStore.bookDao().insert(it)
        }
        logUtil.info("Added book with ID: $bookId")
    }

    fun deleteBookAsync(book: Book) = scope.launch {
        val bookDeleted = localStore.bookDao().delete(book)
        if (bookDeleted == 0)
            logUtil.info("Failed to delete book with ID: ${book.id}")
        else
            logUtil.info("Deleted book with ID: ${book.id}")
    }

    fun updateBookAsync(book: Book) = scope.launch {
        val bookUpdated = book.let {
            it.updateCurrentStatus()
            localStore.bookDao().update(it)
        }

        if (bookUpdated == 0)
            logUtil.info("Failed to update book with ID: ${book.id}")
        else
            logUtil.info("Updated book with ID: ${book.id}")
    }

    fun countBookByFileUriAsync(fileUri: Uri) = scope.async {
        return@async localStore.bookDao().countByFileUri(fileUri)
    }

    fun getAllBookmarksAsFlow() = localStore.bookmarkDao().getAllAsFlow()

    fun getBookmarkByIdAsync(id: Long) = scope.async {
        return@async localStore.bookmarkDao().getById(id)
    }

    fun insertBookmarkAsync(bookmark: Bookmark) = scope.launch {
        val id = localStore.bookmarkDao().insert(bookmark)
        logUtil.info("Added bookmark with ID: $id")
    }

    fun deleteBookmarkAsync(bookmark: Bookmark) = scope.launch {
        val bookmarkDeleted = localStore.bookmarkDao().delete(bookmark)
        if (bookmarkDeleted == 0)
            logUtil.info("Failed to delete bookmark with ID: ${bookmark.id}")
        else
            logUtil.info("Deleted book with ID: ${bookmark.id}")
    }

    fun updateBookmarkAsync(bookmark: Bookmark) = scope.launch {
        val bookmarkUpdated = localStore.bookmarkDao().update(bookmark)
        if (bookmarkUpdated == 0)
            logUtil.info("Failed to update bookmark with ID: ${bookmark.id}")
        else
            logUtil.info("Updated bookmark with ID: ${bookmark.id}")
    }

    fun getAllBooksWithBookmarksAsFlow() = localStore.bookDao().getAllWithBookmarksAsFlow()

    fun getAllBookmarksWithBookAsFlow() = localStore.bookmarkDao().getAllWithBookAsFlow()

    fun getSelectedAmbientSoundAsync(context: Context) = scope.async {
        return@async sharedPrefHelper.sharedPref(context)
            .getString(ConstantUtils.ambientSoundSharedPreferenceKey, null)
            ?.let { return@let AmbientSoundPlayerService.AmbientSoundType.fromStr(it) }
            ?: AmbientSoundPlayerService.AmbientSoundType.RELAXING // TODO: Default as this sound for now
    }

    fun setSelectedAmbientSoundAsync(
        context: Context,
        ambient: AmbientSoundPlayerService.AmbientSoundType
    ) =
        scope.launch {
            with(sharedPrefHelper.sharedPref(context).edit()) {
                putString(ConstantUtils.ambientSoundSharedPreferenceKey, ambient.displayStr)
                apply()
                logUtil.info("Saved selected ambient sound: ${ambient.displayStr}")
            }
        }
}