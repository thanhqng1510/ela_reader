package com.thanhqng1510.bookreadingapp_android.activities.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.FileUtils
import com.thanhqng1510.bookreadingapp_android.utils.MessageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.streams.toList

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val logUtil: LogUtil
) : ViewModel() {
    // All data loaded from DB as flow
    lateinit var bookListData: StateFlow<List<Book>>
    private var collectBookListDataJob: Job

    // Portion of bookListData to render on screen only
    private val _bookListDisplayData = MutableStateFlow<List<Book>>(emptyList())
    val bookListDisplayData = _bookListDisplayData.asStateFlow()

    private var filterStr = ""
    private var sortOpt = SortOptionSpinnerAdapter.SORTBY.default()

    init {
        collectBookListDataJob = collectFlowAsync()
    }

    fun refresh() {
        collectBookListDataJob.cancel()
        collectBookListDataJob = collectFlowAsync()
    }

    fun deleteBookAtIndexAsync(idx: Int) = viewModelScope.async {
        return@async bookListDisplayData.value[idx].let withBook@{ book ->
            return@withBook book.fileUri.path?.let withFilePath@{ filePath ->
                FileUtils.deleteAtPathAsync(filePath, viewModelScope).join()
                return@withFilePath book.thumbnailUri.path?.let { thumbnailPath ->
                    FileUtils.deleteAtPathAsync(thumbnailPath, viewModelScope).join()
                    dataStore.deleteBookAsync(bookListDisplayData.value[idx]).join()
                    return@let MessageUtils.bookDeletedFriendly
                }
            } ?: run {
                logUtil.error("Failed to retrieve path from Uri", true)
                return@run MessageUtils.bookDeleteFailedFriendly
            }
        }
    }

    fun setFilterString(str: String?) {
        filterStr = str ?: ""
        _bookListDisplayData.value = sortedBookList(filteredBookList(bookListData.value))
    }

    private fun filteredBookList(list: List<Book>): List<Book> {
        val filterResult =
            if (filterStr.isEmpty() || list.isEmpty()) list
            else list.stream()
                .filter { book ->
                    book.title.contains(filterStr, ignoreCase = true) || book.authors.any {
                        it.contains(
                            filterStr,
                            ignoreCase = true
                        )
                    }
                }.toList()
        return filterResult
    }

    fun setSortOption(opt: Int) {
        sortOpt = SortOptionSpinnerAdapter.SORTBY.forIndex(opt)
        _bookListDisplayData.value = sortedBookList(_bookListDisplayData.value)
    }

    private fun sortedBookList(list: List<Book>): List<Book> {
        // List to short => No need to sort
        if (list.size <= 1)
            return list

        return when (sortOpt) {
            SortOptionSpinnerAdapter.SORTBY.LAST_READ -> list.sortedByDescending {
                it.lastRead ?: LocalDateTime.MIN
            }
            SortOptionSpinnerAdapter.SORTBY.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
            SortOptionSpinnerAdapter.SORTBY.TITLE -> list.sortedBy { it.title }
        }
    }

    private fun collectFlowAsync(): Job {
        bookListData = dataStore.getAllBooksAsFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        return viewModelScope.launch {
            bookListData.collectLatest {
                _bookListDisplayData.value = sortedBookList(filteredBookList(it))
            }
        }
    }
}