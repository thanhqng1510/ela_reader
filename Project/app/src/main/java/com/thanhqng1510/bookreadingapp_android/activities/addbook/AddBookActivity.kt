package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : AppCompatActivity() {
    @Inject lateinit var dataStore: DataStore

    private lateinit var backBtn: ImageButton
    private lateinit var addBookBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_books)

        init()
        setupCallbacks()
    }

    private fun init() {
        backBtn = findViewById(R.id.back_btn)
        addBookBtn = findViewById(R.id.add_book_btn)
    }

    private fun setupCallbacks() {
        backBtn.setOnClickListener { finish() }
        addBookBtn.setOnClickListener {
            addMockBook()
            finish()
        }
    }

    private fun addMockBook(): Job { // TODO: Should we wait for this func to complete ?
        val book = Book("title", setOf("author1", "author2"), null, 200, null)

        return CoroutineScope(Dispatchers.IO).launch {
            dataStore.insertBook(book)
        }
    }
}