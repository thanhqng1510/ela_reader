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
    @Inject lateinit var dataStore: DataStore

    // Views in layout
    private lateinit var bookList: RecyclerView
    private lateinit var emptyBookListLayout: LinearLayout
    private lateinit var bookListScrollLayout: NestedScrollView
    private lateinit var settingsBtn: ImageButton
    private lateinit var bookCount: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchBar: SearchView
    private lateinit var sortOptionSpinner: Spinner

    // Use to render book list
    private var bookListData = mutableListOf<Book>() // All data loaded from DB
    private var bookListDisplayData = mutableListOf<Book>() // Portion of bookListData used to render
    private val bookListAdapter = BookListAdapter(bookListDisplayData)

    // Use to render sorting options
    private var sortOptionList = SortOptionSpinnerAdapter.SORTBY.values().map { it.dispString }
    private val sortSpinnerAdapter by lazy { // Must delegate to lazy lambda to ensure it runs after onCreate
        SortOptionSpinnerAdapter(android.R.layout.simple_spinner_dropdown_item, this, sortOptionList)
    }

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

        sortOptionSpinner.adapter = sortSpinnerAdapter

        resetDataFromDatastore()
        showAlternativeViewIfBookListEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCallbacks() {
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        refreshLayout.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                resetDataFromDatastore().join()
                synchronized(this) { refreshLayout.isRefreshing = false }
            }
        }
        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextChange(query: String?): Boolean {
                filterBookListData(query)
                return false
            }
        })
        sortOptionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) = sortBookListDisplayData()

            override fun onNothingSelected(parent: AdapterView<*>) = sortOptionSpinner.setSelection(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resetDataFromDatastore(): Job { // Reset all data and states from store to the UI
        return CoroutineScope(Dispatchers.IO).launch {
            bookListData = dataStore.getAllBooks().toMutableList()

            withContext(Dispatchers.Main) {
                synchronized(this) {
                    if (bookListDisplayData.isNotEmpty()) {
                        val prevListSize = bookListDisplayData.size
                        bookListDisplayData.clear()
                        submitBookListDisplayDataChange(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)
                    }
                }
                synchronized(this) {
                    bookListDisplayData.addAll(bookListData)
                    submitBookListDisplayDataChange(BookListAdapter.DATACHANGED.INSERT, 0, bookListData.size)
                }
                synchronized(this) { searchBar.setQuery("", false) }
            }
        }
    }

    // Always call this function when bookListDisplayData is changed
    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitBookListDisplayDataChange(type: BookListAdapter.DATACHANGED, atIdx: Int, range: Int) {
        bookListAdapter.onBookListDataChange(type, atIdx, range)
        bookCount.text = getString(R.string.num_books, bookListDisplayData.size)
        sortBookListDisplayData() // Always sort bookListDisplayData before showing on UI
    }

    private fun showAlternativeViewIfBookListEmpty(): Boolean {
        return bookListData.isEmpty().run {
            if (this) {
                bookListScrollLayout.visibility = View.GONE
                emptyBookListLayout.visibility = View.VISIBLE
            }
            else {
                bookListScrollLayout.visibility = View.VISIBLE
                emptyBookListLayout.visibility = View.GONE
            }
            this
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterBookListData(query: String?) {
        val filterResult = (query ?: "").let { safeQuery ->
            if (safeQuery.isEmpty() || bookListData.isEmpty()) bookListData
            else bookListData.stream().filter { book ->
                book.title.contains(safeQuery, ignoreCase = true) || book.authors.any { it.contains(safeQuery, ignoreCase = true) }
            }.toList()
        }

        if (bookListDisplayData.isNotEmpty()) {
            val prevListSize = bookListDisplayData.size
            bookListDisplayData.clear()
            submitBookListDisplayDataChange(BookListAdapter.DATACHANGED.REMOVE, 0, prevListSize)
        }

        bookListDisplayData.addAll(filterResult)
        submitBookListDisplayDataChange(BookListAdapter.DATACHANGED.INSERT, 0, bookListDisplayData.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortBookListDisplayData() {
        if (bookListDisplayData.size <= 1) // No need to sort
            return

        fun SortOptionSpinnerAdapter.SORTBY.getComparable(): (Book) -> Comparable<*> {
            return when (this) {
                SortOptionSpinnerAdapter.SORTBY.LAST_READ -> { book -> book.status.lastRead ?: LocalDate.MIN }
                SortOptionSpinnerAdapter.SORTBY.DATE_ADDED -> { book -> book.title }
                SortOptionSpinnerAdapter.SORTBY.TITLE -> { book -> book.title }
            }
        }

        val userComparable = SortOptionSpinnerAdapter.SORTBY.forIndex(sortOptionSpinner.selectedItemPosition).getComparable()
        val defaultComparables = SortOptionSpinnerAdapter.SORTBY.values().map { it.getComparable() }.toTypedArray()
        bookListDisplayData.sortWith { b1, b2 -> compareValuesBy(b1, b2, userComparable, *defaultComparables) }

        // Call bookListAdapter.onBookListDataChange instead of submitNewBookListDisplayData to prevent sorting again
        bookListAdapter.onBookListDataChange(BookListAdapter.DATACHANGED.CHANGE,0, bookListDisplayData.size)
    }
}