package com.thanhqng1510.ela_reader.screens.home.library
// TODO: Refactor this and bookmarks tab

import android.app.Activity
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.LibraryTabBinding
import com.thanhqng1510.ela_reader.datastore.DataStore
import com.thanhqng1510.ela_reader.logstore.LogUtil
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.screens.AppViewModel
import com.thanhqng1510.ela_reader.screens.home.HomeScreenDirections
import com.thanhqng1510.ela_reader.screens.home.HomeViewModel
import com.thanhqng1510.ela_reader.utils.activity_utils.EasyActivity
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import com.thanhqng1510.ela_reader.utils.coroutine_utils.CoroutineGroup
import com.thanhqng1510.ela_reader.utils.file_utils.FilePath
import com.thanhqng1510.ela_reader.utils.fragment_utils.RefreshableBaseFragment
import com.thanhqng1510.ela_reader.utils.listener_utils.IOnItemClickAdapterPositionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.streams.toList


@AndroidEntryPoint
class LibraryTab : RefreshableBaseFragment() {
    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val appViewModel: AppViewModel by activityViewModels()

    // Collect job
    private lateinit var collectBookListDataJob: Job
    private lateinit var collectDisplayDataJob: Job

    // Bindings
    private var bindings: LibraryTabBinding? = null

    // Adapters
    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var sortSpinnerAdapter: BookListSortOptionSpinnerAdapter

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        bindings = LibraryTabBinding.inflate(inflater, container, false)
        return bindings!!.root
    }

    override fun cleanUpView() {
        bindings = null
    }

    override suspend fun refresh() {
        collectDisplayDataJob.cancel()
        collectBookListDataJob.cancel()
        setupCollectors()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == bindings!!.bookList.id) {
            (activity as Activity).menuInflater.inflate(R.menu.book_clicked, menu)

            val deleteOption = menu[1]
            deleteOption.title = SpannableString(deleteOption.title).apply {
                setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_book -> {
                bookListAdapter.longClickedPos?.let {
                    (activity as EasyActivity).waitJobShowProgressOverlayAsync {
                        deleteBookAtIndexAsync(it).await()
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
                requireContext().resources.getString(R.string.library_tab_label)
    }

    override fun setupBindings() {
        sortSpinnerAdapter =
            BookListSortOptionSpinnerAdapter.SortOption.values().map { it.displayStr }
                .let { sortOptionList ->
                    BookListSortOptionSpinnerAdapter(
                        bindings!!.sortOptionSpinner,
                        android.R.layout.simple_spinner_item,
                        sortOptionList,
                        requireContext()
                    ).also {
                        it.setDropDownViewResource(R.layout.book_list_sort_spinner_dropdown_layout)
                    }
                }
        bindings!!.sortOptionSpinner.adapter = sortSpinnerAdapter
        bindings!!.sortOptionSpinner.setSelection(viewModel.bookListSortOpt.value.ordinal)

        bookListAdapter = BookListAdapter()
        bookListAdapter.onItemClickListener = object : IOnItemClickAdapterPositionListener {
            override fun onItemClick(view: View, position: Int) {
                val bookData = viewModel.bookListDisplayData.value[position]

                if (bookData.status == Book.BookStatus.ERROR) {
                    showSnackbar(ConstantUtils.bookFetchFailedFriendly)
                    return
                }

                findNavController().navigate(
                    HomeScreenDirections.actionHomeScreenToReaderScreen(
                        bookData.id, bookData.currentPage
                    )
                )
            }
        }
        bindings!!.bookList.adapter = bookListAdapter
        bindings!!.bookList.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(bindings!!.bookList)

        bindings!!.searchBar.setQuery(viewModel.bookListFilterStr.value, false)

        TransitionInflater.from(requireContext()).let {
            enterTransition = it.inflateTransition(R.transition.fade)
            exitTransition = it.inflateTransition(R.transition.fade)
        }

        appViewModel.appBarTitle.value =
            requireContext().resources.getString(R.string.library_tab_label)
    }

    override fun setupCollectors() {
        collectBookListDataJob = collectBookListDataAsync()
        collectDisplayDataJob = collectDisplayDataAsync()
    }

    override fun setupListeners() {
        bindings!!.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.bookListFilterStr.value = query ?: ""
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
                    viewModel.bookListSortOpt.value =
                        BookListSortOptionSpinnerAdapter.SortOption.forIndex(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    val selection = viewModel.bookListSortOpt.value
                    bindings!!.sortOptionSpinner.setSelection(selection.ordinal)
                }
            }
    }

    private fun showEmptyListView() {
        bindings!!.bookListScrollLayout.visibility = View.GONE
        bindings!!.emptyBookListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bindings!!.bookListScrollLayout.visibility = View.VISIBLE
        bindings!!.emptyBookListLayout.visibility = View.GONE
    }

    // Collect job
    private fun collectBookListDataAsync(): Job {
        viewModel.bookListData = dataStore.getAllBooksAsFlow()
            .stateIn(viewModel.viewModelScope, SharingStarted.Eagerly, emptyList())

        return lifecycleScope.launch {
            whenStarted {
                launch {
                    viewModel.bookListData.collectLatest {
                        if (it.isEmpty()) showEmptyListView()
                        else showPopulatedListView()
                    }
                }
                launch {
                    viewModel.bookListData.collectLatest {
                        viewModel.bookListDisplayData.value = sortedBookList(filteredBookList(it))
                    }
                }
            }
        }
    }

    private fun collectDisplayDataAsync() = lifecycleScope.launch {
        whenStarted {
            launch {
                viewModel.bookListFilterStr.collectLatest {
                    viewModel.bookListDisplayData.value =
                        sortedBookList(filteredBookList(viewModel.bookListData.value))
                }
            }
            launch {
                viewModel.bookListSortOpt.collectLatest {
                    viewModel.bookListDisplayData.value =
                        sortedBookList(viewModel.bookListDisplayData.value)
                }
            }
            launch {
                viewModel.bookListDisplayData.collectLatest {
                    bookListAdapter.submitList(it)
                    bindings!!.bookCount.text = getString(R.string.num_books, it.size)
                }
            }
        }
    }

    // Filter helper
    private fun filteredBookList(list: List<Book>): List<Book> {
        return if (viewModel.bookListFilterStr.value.isEmpty() || list.isEmpty()) list
        else list.stream()
            .filter { book ->
                book.title.contains(
                    viewModel.bookListFilterStr.value,
                    ignoreCase = true
                ) || book.authors.any {
                    it.contains(
                        viewModel.bookListFilterStr.value,
                        ignoreCase = true
                    )
                }
            }.toList()
    }

    // Sort helper
    private fun sortedBookList(list: List<Book>): List<Book> {
        // List to short => No need to sort
        if (list.size <= 1)
            return list

        return when (viewModel.bookListSortOpt.value) {
            BookListSortOptionSpinnerAdapter.SortOption.LAST_READ -> list.sortedByDescending {
                it.lastRead ?: LocalDateTime.MIN
            }
            BookListSortOptionSpinnerAdapter.SortOption.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
            BookListSortOptionSpinnerAdapter.SortOption.TITLE -> list.sortedBy { it.title }
        }
    }

    // Delete helper
    private fun deleteBookAtIndexAsync(idx: Int) = lifecycleScope.async {
        viewModel.bookListDisplayData.value[idx].let withBook@{ book ->
            return@withBook book.fileUri.path?.let withFilePath@{ filePath ->
                val coroutines = CoroutineGroup(this)
                coroutines.addSuspendableBlock { FilePath(filePath).deleteAsync(this).join() }

                book.thumbnailUri.path?.let { thumbnailPath ->
                    coroutines.addSuspendableBlock {
                        FilePath(thumbnailPath).deleteAsync(
                            lifecycleScope
                        ).join()
                    }
                    coroutines.addSuspendableBlock {
                        dataStore.deleteBookAsync(viewModel.bookListDisplayData.value[idx]).join()
                    }

                    coroutines.waitAll()
                    ConstantUtils.bookDeletedFriendly
                }
            } ?: run {
                logUtil.error("Failed to retrieve path from Uri", true)
                ConstantUtils.bookDeleteFailedFriendly
            }
        }
    }
}