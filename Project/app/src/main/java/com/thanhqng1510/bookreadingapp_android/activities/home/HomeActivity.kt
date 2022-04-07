// TODO: add effects when pressing something.
package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import kotlin.streams.toList

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Views in layout
    private lateinit var bookList: RecyclerView
    private lateinit var emptyBookListLayout: LinearLayout
    private lateinit var bookListScrollLayout: NestedScrollView
    private lateinit var settingsBtn: ImageButton
    private lateinit var addBooksBtn: ImageButton
    private lateinit var bookCount: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchBar: SearchView
    private lateinit var sortOptionSpinner: Spinner

    // Adapters
    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var sortSpinnerAdapter: SortOptionSpinnerAdapter

    // Portion of bookListData to render on screen only
    private val bookListDisplayData = MutableLiveData<List<Book>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init()
        setupObservers()
        setupCallbacks()
    }

    private fun init() {
        bookList = findViewById(R.id.book_list)
        emptyBookListLayout = findViewById(R.id.empty_book_list_layout)
        bookListScrollLayout = findViewById(R.id.book_list_scroll_layout)
        settingsBtn = findViewById(R.id.settings_btn)
        addBooksBtn = findViewById(R.id.add_btn)
        bookCount = findViewById(R.id.book_count)
        refreshLayout = findViewById(R.id.refresh_layout)
        searchBar = findViewById(R.id.search_bar)

        sortOptionSpinner = findViewById(R.id.sort_option_spinner)
        sortSpinnerAdapter =
            SortOptionSpinnerAdapter.SORTBY.values().map { it.dispString }.let { sortOptionList ->
                SortOptionSpinnerAdapter(
                    sortOptionSpinner,
                    android.R.layout.simple_spinner_item,
                    sortOptionList,
                    this
                ).also {
                    it.setDropDownViewResource(R.layout.sort_spinner_dropdown_layout)
                }
            }
        sortOptionSpinner.adapter = sortSpinnerAdapter

        bookListAdapter = BookListAdapter()
        bookList.adapter = bookListAdapter
        bookList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.bookListData.observe(this) {
            if (it.isEmpty()) showEmptyListView() else showPopulatedListView()
            bookListDisplayData.value = sortBookList(filterBookList(searchBar.query.toString(), it))
            // TODO: Filter and sort when query
        }
        bookListDisplayData.observe(this) {
            bookListAdapter.submitList(it)
            bookCount.text = getString(R.string.num_books, it.size)
        }
    }

    private fun setupCallbacks() {
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        addBooksBtn.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        refreshLayout.setOnRefreshListener {
            viewModel.bookListData.observe(this, object : Observer<List<Book>> {
                override fun onChanged(data: List<Book>) {
                    refreshLayout.isRefreshing = false
                    viewModel.bookListData.removeObserver(this)
                }
            }).also { viewModel.refreshBookListData() }
        }
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.bookListData.value?.let {
                    bookListDisplayData.value = sortBookList(filterBookList(query, it))
                }
                return false
            }
        })
        sortOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                bookListDisplayData.value?.let { bookListDisplayData.value = sortBookList(it) }
                // TODO: Needs to query again while apply sorting
            }

            override fun onNothingSelected(parent: AdapterView<*>) =
                sortOptionSpinner.setSelection(0)
        }
    }

    private fun showEmptyListView() {
        bookListScrollLayout.visibility = View.GONE
        emptyBookListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bookListScrollLayout.visibility = View.VISIBLE
        emptyBookListLayout.visibility = View.GONE
    }

    private fun filterBookList(query: String?, list: List<Book>): List<Book> {
        val filterResult = (query ?: "").let { safeQuery ->
            if (safeQuery.isEmpty() || list.isEmpty()) list.toList()
            else list.stream().filter { book ->
                book.title.contains(safeQuery, ignoreCase = true) || book.authors.any {
                    it.contains(
                        safeQuery,
                        ignoreCase = true
                    )
                }
            }.toList()
        }
        return filterResult
    }

    private fun sortBookList(list: List<Book>): List<Book> {
        if (list.size <= 1) // No need to sort
            return list.toList()

        fun SortOptionSpinnerAdapter.SORTBY.getComparable(): (Book) -> Comparable<*> {
            return when (this) {
                SortOptionSpinnerAdapter.SORTBY.LAST_READ -> { book ->
                    book.status.lastRead ?: LocalDate.MIN
                }
                SortOptionSpinnerAdapter.SORTBY.DATE_ADDED -> { book -> book.dateAdded }
                SortOptionSpinnerAdapter.SORTBY.TITLE -> { book -> book.title }
            }
        }

        val userComparable =
            SortOptionSpinnerAdapter.SORTBY.forIndex(sortOptionSpinner.selectedItemPosition)
                .getComparable()
        val defaultComparables =
            SortOptionSpinnerAdapter.SORTBY.values().map { it.getComparable() }.toTypedArray()

        return list.sortedWith { b1, b2 ->
            compareValuesBy(
                b1,
                b2,
                userComparable,
                *defaultComparables
            )
        }
    }
}