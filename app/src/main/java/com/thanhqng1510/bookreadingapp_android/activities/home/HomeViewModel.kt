package com.thanhqng1510.bookreadingapp_android.activities.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    val bookListData =
        dataStore.getAllBooks().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Portion of bookListData to render on screen only
    private val _bookListDisplayData = MutableStateFlow<List<Book>>(emptyList())
    val bookListDisplayData = _bookListDisplayData.asStateFlow()

    private var filterStr = ""
    private var sortOpt = SortOptionSpinnerAdapter.SORTBY.default()

    init {
        viewModelScope.launch {
            bookListData.collectLatest {
                _bookListDisplayData.value = sortedBookList(filteredBookList(it))
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        // Fake new data to trigger UI observe -> book list adapter will handle the changes
        _bookListDisplayData.value = emptyList()
        delay(1000) // Need to since UI collect with collectLatest ->
        _bookListDisplayData.value = sortedBookList(filteredBookList(bookListData.value))
    }

    fun deleteBookAtIndexAsync(idx: Int) = viewModelScope.async {
        return@async bookListDisplayData.value[idx].let withBook@{ book ->
            return@withBook book.fileUri.path?.let {
                FileUtils.deleteAtPathAsync(it, viewModelScope).join()
                dataStore.deleteBook(bookListDisplayData.value[idx]).join()
                return@let "Book removed from your library"
            } ?: run {
                logUtil.error("Failed to retrieve path from book Uri", true)
                return@run "An error occurred while deleting book"
            }
        }
    }

    fun setFilterString(str: String?) {
        filterStr = str ?: ""
        viewModelScope.launch {
            _bookListDisplayData.value = sortedBookList(filteredBookList(bookListData.value))
        }
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
        viewModelScope.launch {
            _bookListDisplayData.value = sortedBookList(_bookListDisplayData.value)
        }
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
}