package com.thanhqng1510.bookreadingapp_android.activities.home

import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.home.bookmarks.BookmarksFragment
import com.thanhqng1510.bookreadingapp_android.activities.home.library.LibraryFragment
import com.thanhqng1510.bookreadingapp_android.utils.fragment_utils.FragmentProvider

enum class HomeFragment : FragmentProvider {
    LIBRARY {
        override fun getFragment() = LibraryFragment()

        override fun getLayoutResourceId() = R.id.library_page

        override fun getTag(): String = "HomeFragment.LIBRARY"
    },

    // SHARING,
    // NOTES,
    BOOKMARKS {
        override fun getFragment() = BookmarksFragment()

        override fun getLayoutResourceId() = R.id.bookmarks_page

        override fun getTag(): String = "HomeFragment.BOOKMARKS"
    }
}