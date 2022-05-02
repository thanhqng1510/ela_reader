package com.thanhqng1510.ela_reader.screens.home

import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.screens.home.bookmarks.BookmarksTab
import com.thanhqng1510.ela_reader.screens.home.library.LibraryTab
import com.thanhqng1510.ela_reader.utils.fragment_utils.RefreshableFragmentProvider

enum class HomeTabType(val menuItemId: Int) : RefreshableFragmentProvider {
    LIBRARY(R.id.library_tab) {
        override fun getFragment() = LibraryTab()

        override fun getTag(): String = "HomeTabType.LIBRARY"
    },

    // SHARING,
    // NOTES,
    BOOKMARKS(R.id.bookmarks_tab) {
        override fun getFragment() = BookmarksTab()

        override fun getTag(): String = "HomeTabType.BOOKMARKS"
    }
}