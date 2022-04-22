package com.thanhqng1510.bookreadingapp_android.activities.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmarks.Bookmark
import com.thanhqng1510.bookreadingapp_android.utils.ConstantUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.streams.toList

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val dataStore: DataStore,
) : ViewModel() {
    // All data loaded from DB as flow
    lateinit var bookmarkListData: StateFlow<List<Bookmark.BookmarkWithBook>>
    private var collectBookmarkListDataJob: Job

    // Portion of bookmarkListData to render on screen only
    private val _bookmarkListDisplayData =
        MutableStateFlow<List<Bookmark.BookmarkWithBook>>(emptyList())
    val bookmarkListDisplayData = _bookmarkListDisplayData.asStateFlow()

    private var filterStr = ""
    private var sortOpt = BookmarkListSortOptionSpinnerAdapter.SORTBY.default()

    init {
        collectBookmarkListDataJob = collectFlowAsync()
    }

    fun refresh() {
        collectBookmarkListDataJob.cancel()
        collectBookmarkListDataJob = collectFlowAsync()
    }

    fun deleteBookmarkAtIndexAsync(idx: Int) = viewModelScope.async {
        dataStore.deleteBookmarkAsync(bookmarkListDisplayData.value[idx].bookmark).join()
        return@async ConstantUtils.bookmarkDeletedFriendly
    }

    fun setFilterString(str: String?) {
        filterStr = str ?: ""
        _bookmarkListDisplayData.value =
            sortedBookmarkList(filteredBookmarkList(bookmarkListData.value))
    }

    private fun filteredBookmarkList(list: List<Bookmark.BookmarkWithBook>): List<Bookmark.BookmarkWithBook> {
        val filterResult =
            if (filterStr.isEmpty() || list.isEmpty()) list
            else list.stream()
                .filter { data ->
                    data.book.title.contains(
                        filterStr,
                        ignoreCase = true
                    ) || data.book.authors.any {
                        it.contains(
                            filterStr,
                            ignoreCase = true
                        )
                    }
                }.toList()
        return filterResult
    }

    fun setSortOption(opt: Int) {
        sortOpt = BookmarkListSortOptionSpinnerAdapter.SORTBY.forIndex(opt)
        _bookmarkListDisplayData.value = sortedBookmarkList(_bookmarkListDisplayData.value)
    }

    private fun sortedBookmarkList(list: List<Bookmark.BookmarkWithBook>): List<Bookmark.BookmarkWithBook> {
        // List to short => No need to sort
        if (list.size <= 1)
            return list

        return when (sortOpt) {
            BookmarkListSortOptionSpinnerAdapter.SORTBY.DATE_ADDED -> list.sortedByDescending { it.bookmark.dateAdded }
            BookmarkListSortOptionSpinnerAdapter.SORTBY.TITLE -> list.sortedBy { it.book.title }
        }
    }

    private fun collectFlowAsync(): Job {
        bookmarkListData = dataStore.getAllBookmarksWithBookAsFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        return viewModelScope.launch {
            bookmarkListData.collectLatest {
                _bookmarkListDisplayData.value = sortedBookmarkList(filteredBookmarkList(it))
            }
        }
    }
}