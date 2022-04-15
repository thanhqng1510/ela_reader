// TODO: Add viewmodel
package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.viewModels
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : BaseActivity() {
    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: AddBookViewModel by viewModels()

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
                    runJobShowProcessingOverlay { addBook(fileName, fileType, uri) }
                }
            } ?: run {
                logUtil.error("Failed to get file info when add book", true)
                showSnackbar("An error occurred while adding book")
            }
        }
    }

    override fun init() {
        setContentView(R.layout.activity_add_books)

        globalCoordinatorLayout = findViewById(R.id.coordinator_layout)
        backBtn = findViewById(R.id.back_btn)
        addBookBtn = findViewById(R.id.add_book_btn)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        backBtn.setOnClickListener { finish() }
        addBookBtn.setOnClickListener {
            selectFileLauncher.launch(
                arrayOf("application/pdf") // TODO: Only support PDF for now
            )
        }
    }

    private suspend fun addBook(
        fileNameWithExt: String,
        fileType: String,
        contentUri: Uri
    ): String {
        return viewModel.copyBookToAppDirAsync(fileNameWithExt, contentUri).await()
            ?.let { fileUri ->
                val result = viewModel.addBookAsync(fileNameWithExt, fileType, fileUri).await()
                if (!result) {
                    logUtil.error("Failed to add book", true)
                    return@let "An error occurred while adding book"
                }
                return@let "Book added successfully"
            } ?: run {
            logUtil.error("Failed to copy file to app-specific-dir", true)
            return@run "An error occurred while copying book to app directory"
        }
    }
}