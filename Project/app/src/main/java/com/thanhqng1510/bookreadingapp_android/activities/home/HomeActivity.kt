package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.datamodels.Book
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore: DataStore

    private lateinit var bookList: RecyclerView
    private lateinit var settingsBtn: ImageButton
    private lateinit var bookCount: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout

    private var bookListData = mutableListOf<Book>()
    private var bookListAdapter = BookListAdapter(bookListData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBindings()
        setupCallbacks()
    }

    private fun setupBindings() {
        bookList = findViewById(R.id.book_list)
        settingsBtn = findViewById(R.id.settings_btn)
        bookCount = findViewById(R.id.book_count)
        refreshLayout = findViewById(R.id.refresh_layout)

        bookCount.text = getString(R.string.num_books, bookListData.size)
        bookList.adapter = bookListAdapter
        bookList.layoutManager = LinearLayoutManager(this)

        loadBookListData()
    }

    private fun setupCallbacks() {
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        refreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                loadBookListData().join()
                synchronized(this) {
                    refreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun loadBookListData(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            val newData = dataStore.getBookListAsync().await()

            withContext(Dispatchers.Main) {
                synchronized(this) {
                    val prevListSize = bookListData.size
                    bookListData.clear()
                    onBookListDataChange(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)
                }

                synchronized(this) {
                    bookListData.addAll(newData)
                    onBookListDataChange(BookListAdapter.DATACHANGED.INSERT, 0, newData.size)
                }
            }
        }
    }

    private fun onBookListDataChange(type: BookListAdapter.DATACHANGED, atIdx: Int, size: Int) {
        bookListAdapter.onBookListDataChange(type, atIdx, size)
        bookCount.text = getString(R.string.num_books, bookListData.size)
    }
}