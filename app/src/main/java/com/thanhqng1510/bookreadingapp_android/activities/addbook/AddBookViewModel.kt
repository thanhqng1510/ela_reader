package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.application.MainApplication
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil,
    application: Application
) : AndroidViewModel(application) {
    fun addBookToDatabaseAsync(fileNameWithExt: String, fileType: String, fileUri: Uri) =
        viewModelScope.async {
            if (!FileUtils.isUriOfFile(fileUri)) {
                logUtil.error("Attempt to save Uri with invalid scheme to database", true)
                return@async false
            }

            fileUri.path?.let { filePath ->
                val thumbnailUri = async(Dispatchers.IO) thumbnail@{
                    val booksDir =
                        "${getApplication<Application>().getExternalFilesDir(null)}/${MainApplication.booksExternalDir}"
                    val thumbnailFile = File(booksDir, "$fileNameWithExt.bm")

                    FileOutputStream(thumbnailFile).use { out ->
                        FileUtils.getPdfThumbnail(filePath)
                            .compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    return@thumbnail thumbnailFile.toUri()
                }

                val book = Book(
                    FileUtils.getFileDisplayName(fileNameWithExt),
                    setOf("abc", "def"), // TODO: Get authors
                    thumbnailUri.await(),
                    FileUtils.getPdfPageCount(filePath),
                    LocalDateTime.now(),
                    fileType,
                    fileUri,
                    null
                )

                dataStore.insertBook(book).join()
                return@async true
            } ?: run {
                logUtil.error("Failed to get file path from Uri", true)
                return@run false
            }
        }

    fun copyBookToAppDirAsync(fileNameWithExt: String, uri: Uri): Deferred<Uri?> {
        val booksDir =
            "${getApplication<Application>().getExternalFilesDir(null)}/${MainApplication.booksExternalDir}"

        return viewModelScope.async(Dispatchers.IO) {
            val newFile = File(booksDir, fileNameWithExt)
            if (newFile.exists()) {
                logUtil.error("Book already existed in app-specific-dir", false)
                return@async null
            }

            val outputStream = FileOutputStream(newFile)
            getApplication<Application>().contentResolver.openInputStream(uri)
                ?.let { inputStream ->
                    FileUtils.copyTo(inputStream, outputStream, viewModelScope).join()
                    return@async newFile.toUri()
                } ?: run {
                logUtil.error("Failed to get input stream from Uri", true)
                return@async null
            }
        }
    }
}