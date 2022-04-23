package com.thanhqng1510.bookreadingapp_android.activities.addbook_activity

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
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FileName
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FileUtils.copyToAsync
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FileUtils.isFileScheme
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.PDFFilePath
import dagger.hilt.android.lifecycle.HiltViewModel
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
            if (!fileUri.isFileScheme()) {
                logUtil.error("Attempt to save Uri with invalid scheme to database", true)
                return@async false
            }

            fileUri.path?.let { filePath ->
                val pdfFilePath = PDFFilePath(filePath)
                val fileName = FileName(fileNameWithExt)

                val thumbnailUri = async(Dispatchers.IO) thumbnail@{
                    val thumbnailFile = File(
                        getApplication<MainApplication>().externalBooksDir,
                        "$fileNameWithExt.bm"
                    )

                    FileOutputStream(thumbnailFile).use { out ->
                        pdfFilePath.getPdfThumbnail().compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    thumbnailFile.toUri()
                }

                val book = Book(
                    fileName.getDisplayName(),
                    setOf("Author"), // TODO: Get authors
                    thumbnailUri.await(),
                    pdfFilePath.getPdfPageCount(),
                    LocalDateTime.now(),
                    fileType,
                    fileUri,
                    null
                )

                dataStore.insertBookAsync(book).join()
                true
            } ?: run {
                logUtil.error("Failed to get file path from Uri", true)
                false
            }
        }

    fun copyBookToAppDirAsync(fileNameWithExt: String, uri: Uri) =
        viewModelScope.async(Dispatchers.IO) {
            val newFile = File(getApplication<MainApplication>().externalBooksDir, fileNameWithExt)
            if (newFile.exists()) {
                logUtil.error("Book already existed in app-specific-dir", false)
                return@async null
            }

            val outputStream = FileOutputStream(newFile)
            getApplication<MainApplication>().contentResolver.openInputStream(uri)
                ?.let { inputStream ->
                    inputStream.copyToAsync(outputStream, viewModelScope).join()
                    newFile.toUri()
                } ?: run {
                logUtil.error("Failed to get input stream from Uri", true)
                null
            }
        }
}