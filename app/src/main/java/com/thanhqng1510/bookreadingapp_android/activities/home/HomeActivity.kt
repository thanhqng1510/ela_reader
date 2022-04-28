package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook_activity.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.default_activity.DefaultActivity
import com.thanhqng1510.bookreadingapp_android.activities.home.bookmarks.BookmarksFragment
import com.thanhqng1510.bookreadingapp_android.activities.home.library.LibraryFragment
import com.thanhqng1510.bookreadingapp_android.activities.settings_activity.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityHomeBinding
import com.thanhqng1510.bookreadingapp_android.utils.fragment_utils.FragmentProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : DefaultActivity() {
    enum class PAGE : FragmentProvider {
        LIBRARY {
            override fun provideFragment() = LibraryFragment()
        },

        // SHARING,
        // NOTES,
        BOOKMARKS {
            override fun provideFragment() = BookmarksFragment()
        }
    }

    /// Bindings
    private lateinit var bindings: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings.bottomNavigation.selectedItemId =
            R.id.library_page // Need to set here so our listener will be called
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        bindings.addBtn.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        bindings.refreshLayout.setOnRefreshListener { bindings.refreshLayout.isRefreshing = false }
        bindings.bottomNavigation.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener when (item.itemId) {
                R.id.library_page -> {
                    setCurrentPage(PAGE.LIBRARY)
                    true
                }
                R.id.bookmarks_page -> {
                    setCurrentPage(PAGE.BOOKMARKS)
                    true
                }
                else -> false
            }
        }
    }

    private fun setCurrentPage(fragmentProvider: FragmentProvider) =
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.page_fragment, fragmentProvider.provideFragment())
            // TODO: Replace is ok for now since fragments don't contain any data to be reloaded
        }
}