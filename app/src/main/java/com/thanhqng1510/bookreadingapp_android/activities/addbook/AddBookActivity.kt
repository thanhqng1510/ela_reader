// TODO: Add viewmodel
package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : BaseActivity() {
    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var logUtil: LogUtil

    private lateinit var backBtn: ImageButton
    private lateinit var addBookBtn: ImageButton

    private val selectFileLauncher = registerForActivityResult(OpenDocument()) { nullableUri ->
        nullableUri?.let { uri ->
            contentResolver.getType(uri)?.let { fileType ->
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                }?.let { fileName -> addBook(fileName, fileType, uri) }
            } ?: run {
                logUtil.error("Failed to get file info when add book", true)
                showSnackbar(
                    findViewById(R.id.coordinator_layout),
                    "An error occurred while adding book"
                )
            }
        }
    }

    override fun init() {
        setContentView(R.layout.activity_add_books)

        backBtn = findViewById(R.id.back_btn)
        addBookBtn = findViewById(R.id.add_book_btn)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        backBtn.setOnClickListener { finish() }
        addBookBtn.setOnClickListener {
            selectFileLauncher.launch(
                arrayOf(
                    "text/html",
                    "application/pdf",
                    "application/epub+zip"
                )
            )
        }
    }

    private fun addBook(fileNameWithExt: String, fileType: String, uri: Uri) {
        lifecycleScope.launch {
            showLoadingOverlay()

            val bookDataDir = "${getExternalFilesDir(null)}/books/"
            val newUri = async(Dispatchers.IO) {
                with(File(bookDataDir)) {
                    if (!exists()) mkdir()

                    val newFile = File(bookDataDir, fileNameWithExt)
                    if (newFile.exists())
                        return@async null

                    val outputStream = FileOutputStream(newFile)
                    contentResolver.openInputStream(uri)?.let { inputStream ->
                        var read: Int
                        val bufferSize = 1024
                        val buffers = ByteArray(bufferSize)
                        while (inputStream.read(buffers).also { read = it } != -1) {
                            outputStream.write(buffers, 0, read)
                        }
                        inputStream.close()
                        outputStream.close()

                        return@async newFile.toUri()
                    }
                }
            }

            val message = newUri.await()?.let {
                val book = Book(
                    FileUtils.getFileDisplayName(fileNameWithExt),
                    setOf("abc", "def"), // TODO: Get authors
                    null, // TODO: Get cover image
                    200, // TODO: Get num pages
                    LocalDateTime.now(),
                    fileType,
                    it,
                    null
                )
                dataStore.insertBook(book).join()
                return@let "Book added to your library"
            } ?: run {
                logUtil.error("Failed to copy file to app-specific-dir", true)
                return@run "An error occurred while adding book"
            }

            hideLoadingOverlay()
            showSnackbar(findViewById(R.id.coordinator_layout), message)
        }
    }
}