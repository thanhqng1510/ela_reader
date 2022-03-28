package com.thanhqng1510.bookreadingapp_android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.BookListAdapter
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.mocks.MockBooks
import android.R
import android.view.View


class HomeActivity : AppCompatActivity() {
    private lateinit var bookList: RecyclerView
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
        bookList.adapter = BookListAdapter(this, MockBooks.getBooks())
    }

    private fun setupCallbacks() {

    }
}