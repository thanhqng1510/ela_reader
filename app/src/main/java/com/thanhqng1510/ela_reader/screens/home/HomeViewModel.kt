package com.thanhqng1510.ela_reader.screens.home

import androidx.lifecycle.ViewModel
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.models.entities.bookmark.BookmarkWithBook
import com.thanhqng1510.ela_reader.screens.home.bookmarks.BookmarkListSortOptionSpinnerAdapter
import com.thanhqng1510.ela_reader.screens.home.library.BookListSortOptionSpinnerAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // Current attached fragment on HomeActivity
    // View model can only safe fragment's type not fragment's instance since view model do not own any fragments
    var currentTabType: HomeTabType? = null

    // All data loaded from DB as flow
    lateinit var bookListData: StateFlow<List<Book>>
    lateinit var bookmarkListData: StateFlow<List<BookmarkWithBook>>

    // Portion of data to render on screen only
    val bookListDisplayData = MutableStateFlow<List<Book>>(emptyList())
    val bookmarkListDisplayData = MutableStateFlow<List<BookmarkWithBook>>(emptyList())

    // Current filter string and sort option
    val bookListFilterStr = MutableStateFlow("")
    val bookmarkListFilterStr = MutableStateFlow("")

    val bookListSortOpt = MutableStateFlow(BookListSortOptionSpinnerAdapter.SortOption.default())
    val bookmarkListSortOpt =
        MutableStateFlow(BookmarkListSortOptionSpinnerAdapter.SortOption.default())
}