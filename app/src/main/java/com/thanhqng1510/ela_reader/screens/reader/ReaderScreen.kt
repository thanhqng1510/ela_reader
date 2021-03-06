package com.thanhqng1510.ela_reader.screens.reader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.navArgs
import com.danjdt.pdfviewer.PdfViewer
import com.danjdt.pdfviewer.interfaces.OnErrorListener
import com.danjdt.pdfviewer.interfaces.OnPageChangedListener
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.ReaderScreenBinding
import com.thanhqng1510.ela_reader.services.AmbientSoundPlayerService
import com.thanhqng1510.ela_reader.utils.activity_utils.BaseActivity
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

// TODO: This is an activity for now
// TODO: Support scrollbar for this and home screen
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
            R.id.change_ambient_sound_btn -> {
                val soundTypes =
                    AmbientSoundPlayerService.AmbientSoundType.values().map { it.displayStr }
                        .toTypedArray()

                AlertDialog.Builder(this).apply {
                    setIcon(R.drawable.ambient_sound_light)
                    setTitle(resources.getString(R.string.select_ambient_type))
                    setSingleChoiceItems(
                        soundTypes,
                        viewModel.selectedAmbientSoundType.value?.ordinal ?: -1
                    ) { _, selected ->
                        viewModel.selectedAmbientSoundType.value =
                            AmbientSoundPlayerService.AmbientSoundType.fromOrdinal(selected)
                    }
                    setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
                    create().show()
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        viewModel.currentBookmarkIconId =
            if (viewModel.currentBookmarkIconId == R.drawable.bookmark_collection_light) R.drawable.bookmark_added
            else R.drawable.bookmark_collection_light

        menu?.get(1)?.icon =
            ResourcesCompat.getDrawable(resources, viewModel.currentBookmarkIconId, null)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBookAsync(args.bookId, args.bookPage)
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ReaderScreenBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)

        setSupportActionBar(bindings.appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setupCollectors() {
        lifecycleScope.launch {
            whenStarted {
                viewModel.bookmarksFlow.collectLatest {
                    if (needSwitchBookmarkIcon())
                        invalidateOptionsMenu()
                }
            }
        }
    }

    override fun setupListeners() {}

    private fun needSwitchBookmarkIcon(): Boolean {
        val bookmarkAdded =
            viewModel.bookmarksFlow.value.any { b -> b.page == viewModel.bookData?.currentPage }
        return ((bookmarkAdded && viewModel.currentBookmarkIconId == R.drawable.bookmark_collection_light) ||
                (!bookmarkAdded && viewModel.currentBookmarkIconId == R.drawable.bookmark_added))
    }

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
                        if (needSwitchBookmarkIcon())
                            invalidateOptionsMenu()
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