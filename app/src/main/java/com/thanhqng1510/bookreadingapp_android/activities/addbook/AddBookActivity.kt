package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.viewModels
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityAddBooksBinding
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.utils.MessageUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : BaseActivity() {
    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: AddBookViewModel by viewModels()

    private lateinit var bindings: ActivityAddBooksBinding

    private val selectFileLauncher = registerForActivityResult(object : OpenDocument() {
        override fun createIntent(context: Context, input: Array<String>): Intent =
            super.createIntent(context, input).addCategory(Intent.CATEGORY_OPENABLE)
    }) { nullableUri ->
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
                showSnackbar(MessageUtils.bookAddFailedFriendly)
            }
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityAddBooksBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = findViewById(R.id.coordinator_layout)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings.backBtn.setOnClickListener { finish() }
        bindings.addBookBtn.setOnClickListener {
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
            ?.let { persistedUri ->
                val result =
                    viewModel.addBookToDatabaseAsync(fileNameWithExt, fileType, persistedUri)
                        .await()
                if (!result) {
                    logUtil.error("Failed to add book", true)
                    return@let MessageUtils.bookAddFailedFriendly
                }
                return@let MessageUtils.bookAddedFriendly
            } ?: run {
            logUtil.error("Failed to copy file to app-specific-dir", true)
            return@run MessageUtils.bookAddFailedFriendly
        }
    }
}