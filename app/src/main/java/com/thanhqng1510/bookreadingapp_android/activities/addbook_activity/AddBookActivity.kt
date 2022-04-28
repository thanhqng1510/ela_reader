package com.thanhqng1510.bookreadingapp_android.activities.addbook_activity

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments
import androidx.activity.viewModels
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.default_activity.DefaultActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityAddBooksBinding
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.utils.constant_utils.ConstantUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : DefaultActivity() {
    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: AddBookViewModel by viewModels()

    private lateinit var bindings: ActivityAddBooksBinding

    private val selectFileLauncher = registerForActivityResult(OpenMultipleDocuments()) { uriList ->
        waitJobShowProgressOverlayAsync {
            uriList.forEach { uri ->
                contentResolver.getType(uri)?.let { fileType ->
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor.moveToFirst()
                        cursor.getString(nameIndex)
                    }?.let { fileName -> addBook(fileName, fileType, uri) }
                } ?: run {
                    logUtil.error("Failed to get file info when add book", true)
                    showSnackbar(ConstantUtils.bookAddFailedFriendly)
                }
            }
            ConstantUtils.bookAddedFriendly
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityAddBooksBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)
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
    ) = viewModel.copyBookToAppDirAsync(fileNameWithExt, contentUri).await()
        ?.let { persistedUri ->
            val isSucceed =
                viewModel.addBookToDatabaseAsync(fileNameWithExt, fileType, persistedUri)
                    .await()
            if (!isSucceed) {
                logUtil.error("Failed to add book", true)
                return@let ConstantUtils.bookAddFailedFriendly
            }
            ConstantUtils.bookAddedFriendly
        } ?: run {
        logUtil.error("Failed to copy file to app-specific-dir", true)
        ConstantUtils.bookAddFailedFriendly
    }
}