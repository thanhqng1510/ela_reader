package com.thanhqng1510.bookreadingapp_android.activities.addbook_activity

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments
import androidx.activity.viewModels
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityAddBooksBinding
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.BaseActivity
import com.thanhqng1510.bookreadingapp_android.utils.constant_utils.ConstantUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : BaseActivity() {
    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: AddBookViewModel by viewModels()

    private lateinit var bindings: ActivityAddBooksBinding

    private val selectFileLauncher = registerForActivityResult(OpenMultipleDocuments()) { uriList ->
        waitJobShowProgressOverlayAsync {
            var succeeded = true
            uriList.forEach { uri ->
                // Use and() instead of && so the expression is not skipped if succeeded is false
                succeeded = succeeded.and(contentResolver.getType(uri)?.let { fileType ->
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor.moveToFirst()
                        cursor.getString(nameIndex)
                    }?.let { fileName -> addBook(fileName, fileType, uri) }
                } ?: run {
                    logUtil.error("Failed to get file info when add book", true)
                    showSnackbar(ConstantUtils.bookAddFailedFriendly)
                    false
                })
            }
            if (uriList.isNotEmpty()) {
                if (succeeded) ConstantUtils.bookAddedFriendly
                else ConstantUtils.bookAddFailedFriendly
            } else null
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
                return@let false
            }
            true
        } ?: run {
        logUtil.error("Failed to copy file to app-specific-dir", true)
        false
    }
}