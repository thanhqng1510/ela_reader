package com.thanhqng1510.ela_reader.screens.reader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.navArgs
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.interfaces.OnErrorListener
import com.danjdt.pdfviewer.interfaces.OnPageChangedListener
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.ReaderScreenBinding
import com.thanhqng1510.ela_reader.utils.activity_utils.BaseActivity
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

// TODO: This is an activity for now
@AndroidEntryPoint
class ReaderScreen : BaseActivity() {
    private val viewModel: ReaderViewModel by viewModels()

    private lateinit var bindings: ReaderScreenBinding

    private val args: ReaderScreenArgs by navArgs()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.reader_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_bookmark_btn -> {
                waitJobShowProgressOverlayAsync {
                    viewModel.addBookmark()
                    ConstantUtils.bookmarkAddedFriendly
                }
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBookAsync(args.bookId, args.bookPage)
        viewModel.playAmbientSoundAsync()
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ReaderScreenBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)

        setSupportActionBar(bindings.appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {}

    private fun showBookAsync(bookId: Long, bookPage: Int) = lifecycleScope.launch {
        whenStarted {
            val errMsg = viewModel.getBookByIdAsync(bookId).await()

            if (errMsg.isNotEmpty()) // TODO: Pass error back to parent
                finish()

            PdfViewer.ConfigBuilder(bindings.mainBody)
                .setZoomEnabled(true)
                .setMaxZoom(5f)
                .setOnPageChangedListener(object : OnPageChangedListener {
                    override fun onPageChanged(page: Int, total: Int) {
                        viewModel.bookData?.currentPage = page
                    }
                })
                .setOnErrorListener(object : OnErrorListener {
                    override fun onFileLoadError(e: Exception) {
                        runOnUiThread { finish() }
                    }

                    override fun onAttachViewError(e: Exception) {
                        runOnUiThread { finish() }
                    }

                    override fun onPdfRendererError(e: IOException) {
                        runOnUiThread { finish() }
                    }
                })
                .build()
                .load(viewModel.bookData!!.fileUri, bookPage)
        }
    }
}