package com.thanhqng1510.bookreadingapp_android.activities.home.library

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
import com.thanhqng1510.bookreadingapp_android.activities.home.HomeViewModel
import com.thanhqng1510.bookreadingapp_android.activities.reader_activity.ReaderActivity
import com.thanhqng1510.bookreadingapp_android.databinding.FragmentLibraryBinding
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.EasyActivity
import com.thanhqng1510.bookreadingapp_android.utils.constant_utils.ConstantUtils
import com.thanhqng1510.bookreadingapp_android.utils.coroutine_utils.CoroutineGroup
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FilePath
import com.thanhqng1510.bookreadingapp_android.utils.fragment_utils.RefreshableBaseFragment
import com.thanhqng1510.bookreadingapp_android.utils.listener_utils.IOnItemClickAdapterPositionListener
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
class LibraryFragment : RefreshableBaseFragment() {
    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var logUtil: LogUtil

    // View model
    private val viewModel: HomeViewModel by activityViewModels()

    // Collect job
    private lateinit var collectBookListDataJob: Job
    private lateinit var collectDisplayDataJob: Job

    // Bindings
    private var _bindings: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val bindings get() = _bindings!!

    // Adapters
    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var sortSpinnerAdapter: BookListSortOptionSpinnerAdapter

    private var openBookLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(ReaderActivity.showBookResultExtra)?.let {
                    (activity as EasyActivity).showSnackbar(it)
                }
            }
        }

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        _bindings = FragmentLibraryBinding.inflate(inflater, container, false)
        return bindings.root
    }

    override fun cleanUpView() {
        _bindings = null
    }

    override suspend fun refresh() {
        collectBookListDataJob.cancel()
        collectBookListDataJob = collectBookListDataAsync()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == bindings.bookList.id) {
            (activity as Activity).menuInflater.inflate(R.menu.book_list_menu, menu)
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

    override fun setupBindings(savedInstanceState: Bundle?) {
        sortSpinnerAdapter =
            BookListSortOptionSpinnerAdapter.SortOption.values().map { it.displayStr }
                .let { sortOptionList ->
                    BookListSortOptionSpinnerAdapter(
                        bindings.sortOptionSpinner,
                        android.R.layout.simple_spinner_item,
                        sortOptionList,
                        requireContext()
                    ).also {
                        it.setDropDownViewResource(R.layout.book_list_sort_spinner_dropdown_layout)
                    }
                }
        bindings.sortOptionSpinner.adapter = sortSpinnerAdapter
        bindings.sortOptionSpinner.setSelection(viewModel.bookListSortOpt.value.ordinal)

        bookListAdapter = BookListAdapter()
        bookListAdapter.onItemClickListener = object : IOnItemClickAdapterPositionListener {
            override fun onItemClick(view: View, position: Int) {
                val bookData = viewModel.bookListDisplayData.value[position]

                if (bookData.status == Book.BookStatus.ERROR) {
                    (activity as EasyActivity).showSnackbar(ConstantUtils.bookFetchFailedFriendly)
                    return
                }

                val intent = Intent(context, ReaderActivity::class.java)
                intent.putExtra(ReaderActivity.bookIdExtra, bookData.id)
                openBookLauncher.launch(intent)
            }
        }
        bindings.bookList.adapter = bookListAdapter
        bindings.bookList.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(bindings.bookList)

        bindings.searchBar.setQuery(viewModel.bookListFilterStr.value, false)
    }

    override fun setupCollectors() {
        collectBookListDataJob = collectBookListDataAsync()
        collectDisplayDataJob = collectDisplayDataAsync()
    }

    override fun setupListeners() {
        bindings.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.bookListFilterStr.value = query ?: ""
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
                    viewModel.bookListSortOpt.value =
                        BookListSortOptionSpinnerAdapter.SortOption.forIndex(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    val selection = viewModel.bookListSortOpt.value
                    bindings.sortOptionSpinner.setSelection(selection.ordinal)
                }
            }
    }

    private fun showEmptyListView() {
        bindings.bookListScrollLayout.visibility = View.GONE
        bindings.emptyBookListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bindings.bookListScrollLayout.visibility = View.VISIBLE
        bindings.emptyBookListLayout.visibility = View.GONE
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
                    bindings.bookCount.text = getString(R.string.num_books, it.size)
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