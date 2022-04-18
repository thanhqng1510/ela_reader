// TODO: add effects when pressing something.
package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.activities.reader.ReaderActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Bindings
    private lateinit var bindings: ActivityHomeBinding

    // Adapters
    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var sortSpinnerAdapter: SortOptionSpinnerAdapter

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.book_list) {
            menuInflater.inflate(R.menu.book_list_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_book -> {
                bookListAdapter.longClickedPos?.let {
                    runJobShowProcessingOverlay {
                        viewModel.deleteBookAtIndexAsync(it).await()
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = findViewById(R.id.coordinator_layout)

        sortSpinnerAdapter =
            SortOptionSpinnerAdapter.SORTBY.values().map { it.displayStr }.let { sortOptionList ->
                SortOptionSpinnerAdapter(
                    bindings.sortOptionSpinner,
                    android.R.layout.simple_spinner_item,
                    sortOptionList,
                    this
                ).also {
                    it.setDropDownViewResource(R.layout.sort_spinner_dropdown_layout)
                }
            }
        bindings.sortOptionSpinner.adapter = sortSpinnerAdapter

        bookListAdapter = BookListAdapter { _, pos ->
            val bookData = viewModel.bookListDisplayData.value[pos]
            val intent = Intent(this, ReaderActivity::class.java)
            intent.putExtra(ReaderActivity.bookIdExtra, bookData.id)
            startActivity(intent)
        }
        bindings.bookList.adapter = bookListAdapter
        bindings.bookList.layoutManager = LinearLayoutManager(this)
        registerForContextMenu(bindings.bookList)
    }

    override fun setupCollectors() {
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
                        bindings.bookCount.text = getString(R.string.num_books, it.size)
                    }
                }
            }
        }
    }

    override fun setupListeners() {
        bindings.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        bindings.addBtn.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        bindings.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                whenStarted {
                    viewModel.refresh().join()
                    bindings.refreshLayout.isRefreshing = false
                }
            }
        }
        bindings.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.setFilterString(query)
                return false
            }
        })
        bindings.sortOptionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    viewModel.setSortOption(pos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    bindings.sortOptionSpinner.setSelection(0)
                    viewModel.setSortOption(0)
                }
            }
    }

    private fun showEmptyListView() {
        bindings.bookListScrollLayout.visibility = View.GONE
        bindings.emptyBookListLayout.visibility = View.VISIBLE
    }

    private fun showPopulatedListView() {
        bindings.bookListScrollLayout.visibility = View.VISIBLE
        bindings.emptyBookListLayout.visibility = View.GONE
    }
}