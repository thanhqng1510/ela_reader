package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook_activity.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.default_activity.DefaultActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings_activity.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityHomeBinding
import com.thanhqng1510.bookreadingapp_android.utils.fragment_utils.FragmentProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : DefaultActivity() {
    /// Bindings
    private lateinit var bindings: ActivityHomeBinding

    private val addedFragments = mutableMapOf<String, Fragment>()
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings.bottomNavigation.selectedItemId =
            HomeFragment.LIBRARY.getLayoutResourceId() // Need to set here so our listener will be called
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
                HomeFragment.LIBRARY.getLayoutResourceId() -> {
                    setCurrentPage(HomeFragment.LIBRARY)
                    true
                }
                HomeFragment.BOOKMARKS.getLayoutResourceId() -> {
                    setCurrentPage(HomeFragment.BOOKMARKS)
                    true
                }
                else -> false
            }
        }
    }

    private fun setCurrentPage(provider: FragmentProvider) {
        provider.getTag().let { toAdd ->
            val fragment =
                if (!addedFragments.containsKey(toAdd)) provider.getFragment() else addedFragments[toAdd]
            val prevFragment = currentFragment
            currentFragment = fragment

            supportFragmentManager.commit {
                fragment as Fragment

                setReorderingAllowed(true)
                prevFragment?.run { hide(this) }

                if (!addedFragments.containsKey(toAdd))
                    add(R.id.page_fragment, fragment)
                else
                    show(fragment)
            }
        }
    }
}