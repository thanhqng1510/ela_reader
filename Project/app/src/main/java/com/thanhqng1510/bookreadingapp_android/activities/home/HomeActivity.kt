package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.settings.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import com.thanhqng1510.bookreadingapp_android.models.Book
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore: DataStore

    private lateinit var bookList: RecyclerView
    private lateinit var settingsBtn: ImageButton
    private lateinit var bookCount: TextView

    private lateinit var bookListAdapter: BookListAdapter
    private lateinit var bookListData: MutableList<Book>

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

        bookListData = mutableListOf()
        bookListAdapter= BookListAdapter(bookListData)

        CoroutineScope(Dispatchers.IO).launch {
            bookListData.addAll(dataStore.getBookListAsync().await())
            withContext(Dispatchers.Main) {
                onBookListDataChange(BookListAdapter.DATACHANGED.INSERT, 0, bookListData.size)
            }
        }

        bookCount.text = getString(R.string.num_books, bookListData.size)
        bookList.adapter = bookListAdapter
        bookList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCallbacks() {
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onBookListDataChange(type: BookListAdapter.DATACHANGED, atIdx: Int, size: Int) {
        when (type) {
            BookListAdapter.DATACHANGED.INSERT -> bookListAdapter.notifyItemRangeInserted(atIdx, size)
            BookListAdapter.DATACHANGED.REMOVE -> {
                // TODO: handle this case
            }
        }
        bookCount.text = getString(R.string.num_books, bookListData.size)
    }
}