package com.thanhqng1510.ela_reader.screens.add_book

import android.net.Uri
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.AddBooksScreenBinding
import com.thanhqng1510.ela_reader.logstore.LogUtil
import com.thanhqng1510.ela_reader.screens.AppViewModel
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import com.thanhqng1510.ela_reader.utils.fragment_utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBookScreen : BaseFragment() {
    @Inject
    lateinit var logUtil: LogUtil

    private var bindings: AddBooksScreenBinding? = null

    // View model
    private val viewModel: AddBookViewModel by viewModels()

    private val appViewModel: AppViewModel by activityViewModels()

    private val selectFileLauncher = registerForActivityResult(OpenMultipleDocuments()) { uriList ->
        waitJobShowProgressOverlayAsync {
            var succeeded = true
            uriList.forEach { uri ->
                // Use and() instead of && so the expression is not skipped if succeeded is false
                succeeded =
                    succeeded.and(requireActivity().contentResolver.getType(uri)?.let { fileType ->
                        requireActivity().contentResolver.query(uri, null, null, null, null)
                            ?.use { cursor ->
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

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        bindings = AddBooksScreenBinding.inflate(inflater, container, false)
        return bindings!!.root
    }

    override fun setupBindings() {
        appViewModel.appBarTitle.value =
            requireContext().resources.getString(R.string.add_book_screen_label)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings!!.addBookFileBtn.setOnClickListener {
            selectFileLauncher.launch(
                arrayOf("application/pdf") // TODO: Only support PDF for now
            )
        }
        // TODO: Support QR
    }

    override fun cleanUpView() {
        bindings = null
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