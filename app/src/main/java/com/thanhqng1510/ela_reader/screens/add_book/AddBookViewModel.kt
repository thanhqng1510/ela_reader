package com.thanhqng1510.ela_reader.screens.add_book

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.ela_reader.application.MainApplication
import com.thanhqng1510.ela_reader.datastore.DataStore
import com.thanhqng1510.ela_reader.logstore.LogUtil
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.utils.file_utils.FileUtils.copyToAsync
import com.thanhqng1510.ela_reader.utils.file_utils.FileUtils.isFileScheme
import com.thanhqng1510.ela_reader.utils.file_utils.PDFFilePath
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
                val prettyFileName = pdfFilePath.getFileName().getDisplayName()

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
                    prettyFileName,
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