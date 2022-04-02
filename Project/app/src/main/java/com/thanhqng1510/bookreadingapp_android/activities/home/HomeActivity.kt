package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
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
import java.time.LocalDate
import javax.inject.Inject
import kotlin.streams.toList

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    enum class SORTBY(val dispString: String) {
        LAST_READ("Last read"),
        DATE_ADDED("Date added"),
        TITLE("Title")
    }

    @Inject lateinit var dataStore: DataStore

    private lateinit var bookList: RecyclerView
    private lateinit var emptyBookListLayout: LinearLayout
    private lateinit var bookListScrollLayout: NestedScrollView
    private lateinit var settingsBtn: ImageButton
    private lateinit var bookCount: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchBar: SearchView
    private lateinit var sortOptionSpinner: Spinner

    private var bookListData = mutableListOf<Book>()
    private var bookListDisplayData = mutableListOf<Book>()
    private val bookListAdapter = BookListAdapter(bookListDisplayData)
    private var bookListSortBy = 0

    private var sortOptionList = SORTBY.values().map { it.dispString }
    private lateinit var sortSpinnerAdapter: SortOptionSpinnerAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBindings()
        setupCallbacks()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupBindings() {
        bookList = findViewById(R.id.book_list)
        emptyBookListLayout = findViewById(R.id.empty_book_list_layout)
        bookListScrollLayout = findViewById(R.id.book_list_scroll_layout)
        settingsBtn = findViewById(R.id.settings_btn)
        bookCount = findViewById(R.id.book_count)
        refreshLayout = findViewById(R.id.refresh_layout)
        searchBar = findViewById(R.id.search_bar)
        sortOptionSpinner = findViewById(R.id.sort_option_spinner)

        bookCount.text = getString(R.string.num_books, bookListData.size)
        bookList.adapter = bookListAdapter
        bookList.layoutManager = LinearLayoutManager(this)

        sortSpinnerAdapter =  SortOptionSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptionList)
        sortOptionSpinner.adapter = sortSpinnerAdapter

        refreshBookListView()
        loadBookListData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                withContext(Dispatchers.Main) {
                    synchronized(this) {
                        searchBar.setQuery("", false)
                    }
                }
            }
        }
        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextChange(query: String?): Boolean {
                filterBookListDisplayData(query)
                return false
            }
        })
        sortOptionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                bookListSortBy = pos
                submitNewBookListData(BookListAdapter.DATACHANGED.CHANGE, 0, 0)
                // TODO: Needs enhancement
            }

            override fun onNothingSelected(parent: AdapterView<*>) { sortOptionSpinner.setSelection(0) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadBookListData(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            bookListData = dataStore.getAllBooks().toMutableList()
            withContext(Dispatchers.Main) {
                synchronized(this) {
                    val prevListSize = bookListDisplayData.size
                    bookListDisplayData.clear()
                    submitNewBookListData(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)
                }
                synchronized(this) {
                    bookListDisplayData.addAll(bookListData)
                    submitNewBookListData(BookListAdapter.DATACHANGED.INSERT, 0, bookListData.size)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitNewBookListData(type: BookListAdapter.DATACHANGED, atIdx: Int, size: Int) {
        bookListAdapter.onBookListDataChange(type, atIdx, size)

        bookCount.text = getString(R.string.num_books, bookListDisplayData.size)
        refreshBookListView()

        sortBookDisplayData(bookListSortBy)
        bookListAdapter.onBookListDataChange(BookListAdapter.DATACHANGED.CHANGE, 0, bookListDisplayData.size)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterBookListDisplayData(query: String?) {
        val prevListSize = bookListDisplayData.size
        bookListDisplayData.clear()
        submitNewBookListData(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)

        bookListDisplayData.addAll(bookListData.stream().filter {
                book -> book.title.contains(query ?: "", ignoreCase = true) ||
                        book.authors.any { name -> name.contains(query ?: "", ignoreCase = true) }
        }.toList())
        submitNewBookListData(BookListAdapter.DATACHANGED.INSERT, 0, bookListDisplayData.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortBookDisplayData(sortBy: Int) {
        fun SORTBY.getComparable(): (Book) -> Comparable<*> {
            return when (this) {
                SORTBY.LAST_READ -> { book -> book.status.lastRead ?: LocalDate.MIN }
                SORTBY.DATE_ADDED -> { book -> book.title }
                SORTBY.TITLE -> { book -> book.title }
            }
        }

        val firstComparable = SORTBY.values()[sortBy].getComparable()
        val defaultComparables = SORTBY.values().map { it.getComparable() }.toTypedArray()

        bookListDisplayData.sortWith { book1, book2 -> compareValuesBy(book1, book2, firstComparable, *defaultComparables) }
    }
}