package com.thanhqng1510.bookreadingapp_android.datastore

import android.content.Context
import android.net.Uri
import com.thanhqng1510.bookreadingapp_android.datastore.localstore.LocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.NetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper.SharedPrefHelper
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmarks.Bookmark
import com.thanhqng1510.bookreadingapp_android.utils.AudioUtils
import com.thanhqng1510.bookreadingapp_android.utils.ConstantUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
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

    fun getAllBooksAsFlow(): Flow<List<Book>> = localStore.bookDao().getAllAsFlow()

    fun getBookByIdAsync(id: Long): Deferred<Book?> = scope.async {
        return@async localStore.bookDao().getById(id)
    }

    fun insertBookAsync(book: Book) = scope.launch {
        val bookId = localStore.bookDao().insert(book)
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
        val bookUpdated = localStore.bookDao().update(book)
        if (bookUpdated == 0)
            logUtil.info("Failed to update book with ID: ${book.id}")
        else
            logUtil.info("Updated book with ID: ${book.id}")
    }

    fun countBookByFileUriAsync(fileUri: Uri): Deferred<Long> = scope.async {
        return@async localStore.bookDao().countByFileUri(fileUri)
    }

    fun getAllBookmarksAsFlow(): Flow<List<Bookmark>> = localStore.bookmarkDao().getAllAsFlow()

    fun getBookmarkByIdAsync(id: Long): Deferred<Bookmark?> = scope.async {
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

    fun getAllBooksWithBookmarksAsFlow(): Flow<List<Book.BookWithBookmarks>> =
        localStore.bookDao().getAllWithBookmarksAsFlow()

    fun getAllBookmarksWithBookAsFlow(): Flow<List<Bookmark.BookmarkWithBook>> =
        localStore.bookmarkDao().getAllWithBookAsFlow()

    fun getSelectedAmbientSoundAsync(context: Context): Deferred<AudioUtils.AMBIENT?> =
        scope.async {
            return@async sharedPrefHelper.sharedPref(context)
                .getString(ConstantUtils.ambientSoundSharedPreferenceKey, null)
                ?.let { return@let AudioUtils.AMBIENT.fromStr(it) }
                ?: AudioUtils.AMBIENT.RELAXING // TODO: Default as this sound for now
        }

    fun setSelectedAmbientSoundAsync(context: Context, ambient: AudioUtils.AMBIENT) = scope.launch {
        with(sharedPrefHelper.sharedPref(context).edit()) {
            putString(ConstantUtils.ambientSoundSharedPreferenceKey, ambient.displayStr)
            apply()
            logUtil.info("Saved selected ambient sound: ${ambient.displayStr}")
        }
    }
}