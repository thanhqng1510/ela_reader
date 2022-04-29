package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.addbook_activity.AddBookActivity
import com.thanhqng1510.bookreadingapp_android.activities.settings_activity.SettingsActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivityHomeBinding
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.BaseActivity
import com.thanhqng1510.bookreadingapp_android.utils.fragment_utils.RefreshableBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Bindings
    private lateinit var bindings: ActivityHomeBinding

    private val addedFragments = mutableMapOf<HomeFragmentType, RefreshableBaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.currentFragmentType?.let {
            addedFragments[it] =
                supportFragmentManager.findFragmentByTag(it.getTag()) as RefreshableBaseFragment
        } ?: run {
            bindings.bottomNavigation.selectedItemId =
                HomeFragmentType.LIBRARY.getLayoutResourceId() // Need to set here so our listener will be called
        }
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)
        snackbarAnchor = bindings.bottomNavigation
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
        bindings.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                whenStarted {
                    addedFragments[viewModel.currentFragmentType]?.refresh()
                    bindings.refreshLayout.isRefreshing = false
                }
            }
        }
        bindings.bottomNavigation.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener when (item.itemId) {
                HomeFragmentType.LIBRARY.getLayoutResourceId() -> {
                    setCurrentPage(HomeFragmentType.LIBRARY)
                    true
                }
                HomeFragmentType.BOOKMARKS.getLayoutResourceId() -> {
                    setCurrentPage(HomeFragmentType.BOOKMARKS)
                    true
                }
                else -> false
            }
        }
    }

    private fun setCurrentPage(type: HomeFragmentType) {
        val fragment = addedFragments[type] ?: type.getFragment()
        val prevFragmentType = viewModel.currentFragmentType
        viewModel.currentFragmentType = type

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            prevFragmentType?.run { hide(addedFragments[this] ?: throw IllegalArgumentException()) }

            if (!addedFragments.containsKey(type)) {
                add(bindings.pageFragment.id, fragment, type.getTag())
                addedFragments[type] = fragment
            } else
                show(fragment)
        }
    }
}