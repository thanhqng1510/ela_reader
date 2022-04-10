// TODO: add effects when pressing something.
package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore: DataStore

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init()
        setupCollectors()
        setupCallbacks()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.book_list) {
            val inflater = menuInflater
            inflater.inflate(R.menu.book_list_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_book -> {
                bookListDisplayData.value?.get(bookListAdapter.position)?.let {
                    deleteBook(it)
                }
                false
            }
            else -> super.onContextItemSelected(item)
        }
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
        registerForContextMenu(bookList)

    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookListData.collectLatest {
                        if (it.isEmpty()) showEmptyListView()
                        else showPopulatedListView()
                    }
                }
                launch {
                    viewModel.bookListDisplayData.collectLatest {
                        bookListAdapter.submitList(it)
                        bookCount.text = getString(R.string.num_books, it.size)
                    }
                }
            }
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
            // viewModel.refresh() TODO: Implement refresh
            refreshLayout.isRefreshing = false
        }
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.setFilterString(query)
                return false
            }
        })
        sortOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                viewModel.setSortOption(pos)
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

    private fun deleteBook(book: Book): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            dataStore.deleteBook(book)
        }
    }
}