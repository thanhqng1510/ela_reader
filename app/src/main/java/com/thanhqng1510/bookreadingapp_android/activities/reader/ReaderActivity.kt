package com.thanhqng1510.bookreadingapp_android.activities.reader

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.interfaces.OnPageChangedListener
import com.danjdt.pdfviewer.utils.PdfPageQuality
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityReaderBinding
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class ReaderActivity : BaseActivity() {
    companion object {
        const val bookIdExtra = "bookIdExtra"
        const val showBookResultExtra = "showBookResultExtra"
    }

    @Inject
    lateinit var logUtil: LogUtil

    private val viewModel: ReaderViewModel by viewModels()

    private lateinit var bindings: ActivityReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bookId = intent.getSerializableExtra(bookIdExtra) as Long
        showBook(bookId)
    }

    override fun onPause() {
        viewModel.closeBook()
        super.onPause()
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = findViewById(R.id.coordinator_layout)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings.backBtn.setOnClickListener { finish() }
    }

    private fun showBook(bookId: Long) = lifecycleScope.launch {
        whenStarted {
            val errMsg = viewModel.getBookByIdAsync(bookId).await()

            if (errMsg.isNotEmpty())
                finishWithResult(RESULT_OK, mapOf(Pair(showBookResultExtra, errMsg)))
            else {
                PdfViewer.Builder(bindings.mainBody)
                    .quality(PdfPageQuality.QUALITY_1080)
                    .setZoomEnabled(true)
                    .setMaxZoom(5f)
                    .setOnPageChangedListener(object : OnPageChangedListener {
                        override fun onPageChanged(page: Int, total: Int) {
                            viewModel.bookData.currentPage = page
                        }
                    })
                    .build()
                    .load(viewModel.bookData.fileUri)
            }
        }
    }
}