package com.thanhqng1510.bookreadingapp_android.activities.addbook

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore: DataStore

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

    private fun addMockBook() {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val randomTitle = (1..10).map { allowedChars.random() }.joinToString("")
        val randomAuthor1 = (1..5).map { allowedChars.random() }.joinToString("")
        val randomAuthor2 = (1..5).map { allowedChars.random() }.joinToString("")

        val book = Book(
            randomTitle,
            setOf(randomAuthor1, randomAuthor2),
            null,
            200,
            LocalDateTime.now(),
            null
        )

        dataStore.insertBookAsync(book)
    }
}