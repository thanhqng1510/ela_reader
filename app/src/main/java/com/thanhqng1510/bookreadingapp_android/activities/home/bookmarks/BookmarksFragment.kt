package com.thanhqng1510.bookreadingapp_android.activities.home.bookmarks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.default_activity.DefaultActivity
import com.thanhqng1510.bookreadingapp_android.activities.default_activity.DefaultFragment
import com.thanhqng1510.bookreadingapp_android.activities.home.HomeViewModel
import com.thanhqng1510.bookreadingapp_android.activities.reader_activity.ReaderActivity
import com.thanhqng1510.bookreadingapp_android.databinding.FragmentBookmarksBinding
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.BookmarkWithBook
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.BaseActivity
import com.thanhqng1510.bookreadingapp_android.utils.constant_utils.ConstantUtils
import com.thanhqng1510.bookreadingapp_android.utils.coroutine_utils.CoroutineUtils.retry
import com.thanhqng1510.bookreadingapp_android.utils.listener_utils.IOnItemClickAdapterPositionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.streams.toList

@AndroidEntryPoint
class BookmarksFragment : DefaultFragment() {
    @Inject
    lateinit var dataStore: DataStore

    // View model
    private val viewModel: HomeViewModel by activityViewModels()

    // Collect job
    private lateinit var collectBookmarkListDataJob: Job
    private lateinit var collectDisplayDataJob: Job

    // Bindings
    private var _bindings: FragmentBookmarksBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val bindings get() = _bindings!!

    // Adapters
    private lateinit var bookmarkListAdapter: BookmarkListAdapter
    private lateinit var sortSpinnerAdapter: BookmarkListSortOptionSpinnerAdapter

    private var openBookLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(ReaderActivity.showBookResultExtra)?.let {
                    (activity as BaseActivity).showSnackbar(it)
                }
            }
        }

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        _bindings = FragmentBookmarksBinding.inflate(inflater, container, false)
        return bindings.root
    }

    override fun cleanUpView() {
        _bindings = null
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.bookmark_list) {
            (activity as Activity).menuInflater.inflate(R.menu.bookmark_list_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bookmark -> {
                bookmarkListAdapter.longClickedPos?.let {
                    (activity as DefaultActivity).waitJobShowProgressOverlayAsync {
                        deleteBookmarkAtIndexAsync(it).await()
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        sortSpinnerAdapter =
            BookmarkListSortOptionSpinnerAdapter.SortOption.values().map { it.displayStr }
                .let { sortOptionList ->
                    BookmarkListSortOptionSpinnerAdapter(
                        bindings.sortOptionSpinner,
                        android.R.layout.simple_spinner_item,
                        sortOptionList,
                        requireContext()
                    ).also {
                        it.setDropDownViewResource(R.layout.bookmark_list_sort_spinner_dropdown_layout)
                    }
                }
        bindings.sortOptionSpinner.adapter = sortSpinnerAdapter
        bindings.sortOptionSpinner.setSelection(viewModel.bookmarkListSortOpt.value.ordinal)

        bookmarkListAdapter = BookmarkListAdapter(requireContext())
        bookmarkListAdapter.onItemClickListener = object : IOnItemClickAdapterPositionListener {
            override fun onItemClick(view: View, position: Int) {
                val data = viewModel.bookmarkListDisplayData.value[position]

                if (data.book.status == Book.BookStatus.ERROR) {
                    (activity as DefaultActivity).showSnackbar(ConstantUtils.bookmarkFetchFailedFriendly)
                    return
                }

                val intent = Intent(context, ReaderActivity::class.java)
                intent.putExtra(ReaderActivity.bookIdExtra, data.book.id)
                intent.putExtra(ReaderActivity.bookPageExtra, data.bookmark.page)
                openBookLauncher.launch(intent)
            }
        }
        bindings.bookmarkList.adapter = bookmarkListAdapter
        bindings.bookmarkList.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(bindings.bookmarkList)

        bindings.searchBar.setQuery(viewModel.bookmarkListFilterStr.value, false)

//        viewModel.onRefreshTriggeredListener = {
//            collectViewModelDataJob.cancel()
//            viewModel.refreshLibrary()
//            collectViewModelDataJob = collectViewModelDataAsync()
//        }
    }

    override fun setupCollectors() {
        collectBookmarkListDataJob = collectBookmarkListDataAsync()
        collectDisplayDataJob = collectDisplayDataAsync()
    }

    override fun setupListeners() {
        bindings.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.bookmarkListFilterStr.value = query ?: ""
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
                    viewModel.bookmarkListSortOpt.value =
                        BookmarkListSortOptionSpinnerAdapter.SortOption.forIndex(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    val selection = viewModel.bookmarkListSortOpt.value.ordinal
                    bindings.sortOptionSpinner.setSelection(selection)
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

    // Collect job
    private fun collectBookmarkListDataAsync(): Job {
        if (viewModel.bookmarkListData == null)
            viewModel.bookmarkListData = dataStore.getAllBookmarksWithBookAsFlow()
                .stateIn(viewModel.viewModelScope, SharingStarted.Eagerly, emptyList())

        return lifecycleScope.launch {
            whenStarted {
                retry(100, 10) {
                    viewModel.bookmarkListData?.collectLatest {
                        viewModel.bookmarkListDisplayData.value =
                            sortedBookmarkList(filteredBookmarkList(it))
                    } != null
                }
            }
        }
    }

    private fun collectDisplayDataAsync() = lifecycleScope.launch {
        whenStarted {
            launch {
                retry(100, 10) {
                    viewModel.bookmarkListData?.collectLatest {
                        if (it.isEmpty()) showEmptyListView()
                        else showPopulatedListView()
                    } != null
                }
            }
            launch {
                viewModel.bookmarkListDisplayData.collectLatest {
                    bookmarkListAdapter.submitList(it)
                    bindings.bookmarkCount.text = getString(R.string.num_bookmarks, it.size)
                }
            }
            launch {
                viewModel.bookmarkListFilterStr.collectLatest {
                    viewModel.bookmarkListData?.value?.let {
                        viewModel.bookmarkListDisplayData.value =
                            sortedBookmarkList(filteredBookmarkList(it))
                    }
                }
            }
            launch {
                viewModel.bookmarkListSortOpt.collectLatest {
                    viewModel.bookmarkListDisplayData.value =
                        sortedBookmarkList(viewModel.bookmarkListDisplayData.value)
                }
            }
        }
    }

    // Refresh helper
    fun refreshLibrary() {
        collectBookmarkListDataJob.cancel()
        collectBookmarkListDataJob = collectBookmarkListDataAsync()
    }

    // Filter helper
    private fun filteredBookmarkList(list: List<BookmarkWithBook>): List<BookmarkWithBook> {
        return if (viewModel.bookmarkListFilterStr.value.isEmpty() || list.isEmpty()) list
        else list.stream()
            .filter { data ->
                data.book.title.contains(
                    viewModel.bookmarkListFilterStr.value,
                    ignoreCase = true
                ) || data.book.authors.any {
                    it.contains(
                        viewModel.bookmarkListFilterStr.value,
                        ignoreCase = true
                    )
                }
            }.toList()
    }

    // Sort helper
    private fun sortedBookmarkList(list: List<BookmarkWithBook>): List<BookmarkWithBook> {
        // List to short => No need to sort
        if (list.size <= 1)
            return list

        return when (viewModel.bookmarkListSortOpt.value) {
            BookmarkListSortOptionSpinnerAdapter.SortOption.DATE_ADDED -> list.sortedByDescending { it.bookmark.dateAdded }
            BookmarkListSortOptionSpinnerAdapter.SortOption.TITLE -> list.sortedBy { it.book.title }
        }
    }

    // Delete helper
    private fun deleteBookmarkAtIndexAsync(idx: Int) = lifecycleScope.async {
        dataStore.deleteBookmarkAsync(viewModel.bookmarkListDisplayData.value[idx].bookmark).join()
        ConstantUtils.bookmarkDeletedFriendly
    }
}