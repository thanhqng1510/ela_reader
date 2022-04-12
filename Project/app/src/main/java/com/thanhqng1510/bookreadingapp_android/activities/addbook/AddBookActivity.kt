package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.lifecycle.lifecycleScope
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.BaseActivity
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.formatForFileName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
                }?.let { fileName ->
                    val formattedFileName = fileName.formatForFileName()
                    addBook(formattedFileName, fileType, uri)
                }
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

    private fun addBook(fileName: String, fileType: String, uri: Uri) {
        lifecycleScope.launch {
            val message: String
            showLoadingOverlay()

            if (dataStore.countBookByUriAsync(uri).await() == 0L) {
                val fileNameFinal =
                    if (dataStore.countBookByLikedTitleAsync(fileName).await() == 0L) {
                        fileName
                    } else {
                        "$fileName-${LocalDateTime.now()}"
                    }

                val book = Book(
                    fileNameFinal,
                    setOf("abc", "def"), // TODO: Get authors
                    null, // TODO: Get cover image
                    200, // TODO: Get num pages
                    LocalDateTime.now(),
                    fileType,
                    uri,
                    null
                )
                dataStore.insertBook(book).join()
                message = "Book added to your library"
            } else
                message = "This book is already added"

            hideLoadingOverlay()
            showSnackbar(findViewById(R.id.coordinator_layout), message)
        }
    }
}