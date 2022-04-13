package com.thanhqng1510.bookreadingapp_android.activities.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import dagger.hilt.android.lifecycle.HiltViewModel
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