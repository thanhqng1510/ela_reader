package com.thanhqng1510.bookreadingapp_android.activities.bookmark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.activities.home.HomeActivity
import com.thanhqng1510.bookreadingapp_android.activities.reader.ReaderActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityBookmarkBinding
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.ConstantUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkActivity : BaseActivity() {
    // View model
    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var collectViewModelDataJob: Job

    // Bindings
    private lateinit var bindings: ActivityBookmarkBinding

    // Adapters
    private lateinit var bookmarkListAdapter: BookmarkListAdapter
    private lateinit var sortSpinnerAdapter: BookmarkListSortOptionSpinnerAdapter

    private var openBookLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(ReaderActivity.showBookResultExtra)?.let {
                    showSnackbar(it)
                }
            }
        }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.bookmark_list) {
            menuInflater.inflate(R.menu.bookmark_list_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bookmark -> {
                bookmarkListAdapter.longClickedPos?.let {
                    waitJobShowProcessingOverlayAsync {
                        viewModel.deleteBookmarkAtIndexAsync(it).await()
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)

        sortSpinnerAdapter =
            BookmarkListSortOptionSpinnerAdapter.SORTBY.values().map { it.displayStr }
                .let { sortOptionList ->
                    BookmarkListSortOptionSpinnerAdapter(
                        bindings.sortOptionSpinner,
                        android.R.layout.simple_spinner_item,
                        sortOptionList,
                        this
                    ).also {
                        it.setDropDownViewResource(R.layout.bookmark_list_sort_spinner_dropdown_layout)
                    }
                }
        bindings.sortOptionSpinner.adapter = sortSpinnerAdapter

        bookmarkListAdapter = BookmarkListAdapter(this) { _, pos ->
            val data = viewModel.bookmarkListDisplayData.value[pos]

            if (data.book.status == Book.STATUS.ERROR) {
                showSnackbar(ConstantUtils.bookmarkFetchFailedFriendly)
                return@BookmarkListAdapter
            }

            val intent = Intent(this, ReaderActivity::class.java)
            intent.putExtra(ReaderActivity.bookIdExtra, data.book.id)
            intent.putExtra(ReaderActivity.bookPageExtra, data.bookmark.page)
            openBookLauncher.launch(intent)
        }
        bindings.bookmarkList.adapter = bookmarkListAdapter
        bindings.bookmarkList.layoutManager = LinearLayoutManager(this)
        registerForContextMenu(bindings.bookmarkList)

        bindings.bottomNavigation.selectedItemId = R.id.bookmarks_page
    }

    override fun setupCollectors() {
        collectViewModelDataJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookmarkListData.collectLatest {
                        if (it.isEmpty()) showEmptyListView()
                        else showPopulatedListView()
                    }
                }
                launch {
                    viewModel.bookmarkListDisplayData.collectLatest {
                        bookmarkListAdapter.submitList(it)
                        bindings.bookmarkCount.text = getString(R.string.num_bookmarks, it.size)
                    }
                }
            }
        }
    }

    override fun setupListeners() {
        bindings.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        bindings.refreshLayout.setOnRefreshListener {
            collectViewModelDataJob.cancel()
            viewModel.refresh()
            setupCollectors()
            bindings.refreshLayout.isRefreshing = false
        }
        bindings.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.setFilterString(query)
                return false
            }
        })
        bindings.sortOptionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    viewModel.setSortOption(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    bindings.sortOptionSpinner.setSelection(0)
                    viewModel.setSortOption(0)
                }
            }
        bindings.bottomNavigation.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener when (item.itemId) {
                R.id.books_page -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.bookmarks_page -> true
                else -> false
            }
        }
    }

    private fun showEmptyListView() {
        bindings.bookmarkListScrollLayout.visibility = View.GONE
        bindings.emptyBookmarkListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bindings.bookmarkListScrollLayout.visibility = View.VISIBLE
        bindings.emptyBookmarkListLayout.visibility = View.GONE
    }
}