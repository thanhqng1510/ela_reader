package com.thanhqng1510.bookreadingapp_android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.mocks.MockBooks

class LibraryActivity : AppCompatActivity() {
    private lateinit var bookList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        supportActionBar?.hide()

        setupBindings()
    }

    private fun setupBindings() {
        bookList = findViewById(R.id.book_list)
        // bookList.adapter = BookListAdapter(this, android.R.layout.book_list_row_layout, MockBooks.getBooks())
    }
}