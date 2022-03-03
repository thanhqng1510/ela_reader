package com.thanhqng1510.bookreadingapp_android.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.thanhqng1510.bookreadingapp_android.BookListAdapter
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.mocks.MockBooks

class LibraryActivity : AppCompatActivity() {
    private lateinit var bookList: ListView
    private lateinit var searchBar: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        setupBindings()
        setupCallbacks()
    }

    private fun setupBindings() {
        bookList = findViewById(R.id.book_list)
        searchBar = findViewById(R.id.search_bar)
        bookList.adapter = BookListAdapter(this, R.layout.book_list_row_layout, MockBooks.getBooks())
    }

    private fun setupCallbacks() {

    }
}