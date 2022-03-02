package com.thanhqng1510.bookreadingapp_android.activities

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import com.thanhqng1510.bookreadingapp_android.R

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
        // bookList.adapter = BookListAdapter(this, android.R.layout.book_list_row_layout, MockBooks.getBooks())
    }

    private fun setupCallbacks() {

    }
}