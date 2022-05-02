package com.thanhqng1510.ela_reader.screens.home.bookmarks

import android.app.Activity
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.BookmarksTabBinding
import com.thanhqng1510.ela_reader.datastore.DataStore
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.models.entities.bookmark.BookmarkWithBook
import com.thanhqng1510.ela_reader.screens.AppViewModel
import com.thanhqng1510.ela_reader.screens.home.HomeScreenDirections
import com.thanhqng1510.ela_reader.screens.home.HomeViewModel
import com.thanhqng1510.ela_reader.utils.activity_utils.BaseActivity
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import com.thanhqng1510.ela_reader.utils.fragment_utils.RefreshableBaseFragment
import com.thanhqng1510.ela_reader.utils.listener_utils.IOnItemClickAdapterPositionListener
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
class BookmarksTab : RefreshableBaseFragment() {
    @Inject
    lateinit var dataStore: DataStore

    // View model
    private val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val appViewModel: AppViewModel by activityViewModels()

    // Collect job
    private lateinit var collectBookmarkListDataJob: Job
    private lateinit var collectDisplayDataJob: Job

    // Bindings
    private var bindings: BookmarksTabBinding? = null

    // Adapters
    private lateinit var bookmarkListAdapter: BookmarkListAdapter
    private lateinit var sortSpinnerAdapter: BookmarkListSortOptionSpinnerAdapter

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        bindings = BookmarksTabBinding.inflate(inflater, container, false)
        return bindings!!.root
    }

    override fun cleanUpView() {
        bindings = null
    }

    override suspend fun refresh() {
        collectDisplayDataJob.cancel()
        collectBookmarkListDataJob.cancel()
        setupCollectors()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == bindings!!.bookmarkList.id) {
            (activity as Activity).menuInflater.inflate(R.menu.bookmark_clicked, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bookmark -> {
                bookmarkListAdapter.longClickedPos?.let {
                    (activity as BaseActivity).waitJobShowProgressOverlayAsync {
                        deleteBookmarkAtIndexAsync(it).await()
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden)
            appViewModel.appBarTitle.value =
                requireContext().resources.getString(R.string.bookmarks_tab_label)
    }

    override fun setupBindings() {
        sortSpinnerAdapter =
            BookmarkListSortOptionSpinnerAdapter.SortOption.values().map { it.displayStr }
                .let { sortOptionList ->
                    BookmarkListSortOptionSpinnerAdapter(
                        bindings!!.sortOptionSpinner,
                        android.R.layout.simple_spinner_item,
                        sortOptionList,
                        requireContext()
                    ).also {
                        it.setDropDownViewResource(R.layout.bookmark_list_sort_spinner_dropdown_layout)
                    }
                }
        bindings!!.sortOptionSpinner.adapter = sortSpinnerAdapter
        bindings!!.sortOptionSpinner.setSelection(viewModel.bookmarkListSortOpt.value.ordinal)

        bookmarkListAdapter = BookmarkListAdapter(requireContext())
        bookmarkListAdapter.onItemClickListener = object : IOnItemClickAdapterPositionListener {
            override fun onItemClick(view: View, position: Int) {
                val data = viewModel.bookmarkListDisplayData.value[position]

                if (data.book.status == Book.BookStatus.ERROR) {
                    showSnackbar(ConstantUtils.bookmarkFetchFailedFriendly)
                    return
                }

                findNavController().navigate(
                    HomeScreenDirections.actionHomeScreenToReaderScreen(
                        data.book.id, data.bookmark.page
                    )
                )
            }
        }
        bindings!!.bookmarkList.adapter = bookmarkListAdapter
        bindings!!.bookmarkList.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(bindings!!.bookmarkList)

        bindings!!.searchBar.setQuery(viewModel.bookmarkListFilterStr.value, false)

        TransitionInflater.from(requireContext()).let {
            enterTransition = it.inflateTransition(R.transition.fade)
            exitTransition = it.inflateTransition(R.transition.fade)
        }

        appViewModel.appBarTitle.value =
            requireContext().resources.getString(R.string.bookmarks_tab_label)
    }

    override fun setupCollectors() {
        collectBookmarkListDataJob = collectBookmarkListDataAsync()
        collectDisplayDataJob = collectDisplayDataAsync()
    }

    override fun setupListeners() {
        bindings!!.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.bookmarkListFilterStr.value = query ?: ""
                return false
            }
        })
        bindings!!.sortOptionSpinner.onItemSelectedListener =
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
                    bindings!!.sortOptionSpinner.setSelection(selection)
                }
            }
    }

    private fun showEmptyListView() {
        bindings!!.bookmarkListScrollLayout.visibility = View.GONE
        bindings!!.emptyBookmarkListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bindings!!.bookmarkListScrollLayout.visibility = View.VISIBLE
        bindings!!.emptyBookmarkListLayout.visibility = View.GONE
    }

    // Collect job
    private fun collectBookmarkListDataAsync(): Job {
        viewModel.bookmarkListData = dataStore.getAllBookmarksWithBookAsFlow()
            .stateIn(viewModel.viewModelScope, SharingStarted.Eagerly, emptyList())

        return lifecycleScope.launch {
            whenStarted {
                launch {
                    viewModel.bookmarkListData.collectLatest {
                        if (it.isEmpty()) showEmptyListView()
                        else showPopulatedListView()
                    }
                }
                launch {
                    viewModel.bookmarkListData.collectLatest {
                        viewModel.bookmarkListDisplayData.value =
                            sortedBookmarkList(filteredBookmarkList(it))
                    }
                }
            }
        }
    }

    private fun collectDisplayDataAsync() = lifecycleScope.launch {
        whenStarted {
            launch {
                viewModel.bookmarkListDisplayData.collectLatest {
                    bookmarkListAdapter.submitList(it)
                    bindings!!.bookmarkCount.text = getString(R.string.num_bookmarks, it.size)
                }
            }
            launch {
                viewModel.bookmarkListFilterStr.collectLatest {
                    viewModel.bookmarkListDisplayData.value =
                        sortedBookmarkList(filteredBookmarkList(viewModel.bookmarkListData.value))
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