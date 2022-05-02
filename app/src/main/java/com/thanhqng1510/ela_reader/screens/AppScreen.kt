package com.thanhqng1510.ela_reader.screens

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.AppScreenBinding
import com.thanhqng1510.ela_reader.utils.activity_utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppScreen : BaseActivity() {
    private lateinit var bindings: AppScreenBinding

    private lateinit var navController: NavController

    private val viewModel: AppViewModel by viewModels()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = AppScreenBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)

        setSupportActionBar(bindings.appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun setupCollectors() {
        lifecycleScope.launch {
            whenStarted {
                launch {
                    viewModel.appBarTitle.collectLatest {
                        bindings.appBar.title = it
                    }
                }
            }
        }
    }

    override fun setupListeners() {}
}