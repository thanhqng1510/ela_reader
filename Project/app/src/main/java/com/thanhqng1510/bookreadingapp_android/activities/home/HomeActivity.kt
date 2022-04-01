package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.streams.toList

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject lateinit var dataStore: DataStore

    private lateinit var bookList: RecyclerView
    private lateinit var emptyBookListLayout: LinearLayout
    private lateinit var bookListScrollLayout: NestedScrollView
    private lateinit var settingsBtn: ImageButton
    private lateinit var bookCount: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchBar: SearchView

    private var bookListData = mutableListOf<Book>()
    private var bookListDisplayData = mutableListOf<Book>()
    private val bookListAdapter = BookListAdapter(bookListDisplayData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBindings()
        setupCallbacks()
    }

    private fun setupBindings() {
        bookList = findViewById(R.id.book_list)
        emptyBookListLayout = findViewById(R.id.empty_book_list_layout)
        bookListScrollLayout = findViewById(R.id.book_list_scroll_layout)
        settingsBtn = findViewById(R.id.settings_btn)
        bookCount = findViewById(R.id.book_count)
        refreshLayout = findViewById(R.id.refresh_layout)
        searchBar = findViewById(R.id.search_bar)

        bookCount.text = getString(R.string.num_books, bookListData.size)
        bookList.adapter = bookListAdapter
        bookList.layoutManager = LinearLayoutManager(this)

        refreshBookListView()
        loadBookListData()
    }

    private fun setupCallbacks() {
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        refreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                val ensureWaitTimeJob = launch { delay(500L) }
                loadBookListData().join()
                ensureWaitTimeJob.join()

                synchronized(this) {
                    refreshLayout.isRefreshing = false
                }
            }
        }
        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(query: String?): Boolean {
                filterBookListDisplayData(query)
                return false
            }
        })
    }

    private fun loadBookListData(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            bookListData = dataStore.getAllBooks().toMutableList()
            withContext(Dispatchers.Main) {
                synchronized(this) {
                    val prevListSize = bookListDisplayData.size
                    bookListDisplayData.clear()
                    onBookListDataChange(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)
                }
                synchronized(this) {
                    bookListDisplayData.addAll(bookListData)
                    onBookListDataChange(BookListAdapter.DATACHANGED.INSERT, 0, bookListData.size)
                }
            }
        }
    }

    private fun onBookListDataChange(type: BookListAdapter.DATACHANGED, atIdx: Int, size: Int) {
        bookListAdapter.onBookListDataChange(type, atIdx, size)
        bookCount.text = getString(R.string.num_books, bookListDisplayData.size)
        refreshBookListView()
    }

    private fun refreshBookListView() {
        if (bookListData.isEmpty()) {
            bookListScrollLayout.visibility = View.GONE
            emptyBookListLayout.visibility = View.VISIBLE
        }
        else {
            bookListScrollLayout.visibility = View.VISIBLE
            emptyBookListLayout.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun filterBookListDisplayData(query: String?) {
        val prevListSize = bookListDisplayData.size
        bookListDisplayData.clear()
        onBookListDataChange(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)

        bookListDisplayData.addAll(bookListData.stream().filter {
                book -> book.title.contains(query ?: "", ignoreCase = true) ||
                        book.authors.any { name -> name.contains(query ?: "", ignoreCase = true) }
        }.toList())
        onBookListDataChange(BookListAdapter.DATACHANGED.INSERT, 0, bookListDisplayData.size)
    }
}