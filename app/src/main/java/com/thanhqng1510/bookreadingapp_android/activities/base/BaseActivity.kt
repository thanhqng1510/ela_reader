package com.thanhqng1510.bookreadingapp_android.activities.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.snackbar.Snackbar
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.utils.ActivityUtils
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupCollectors()
        setupListeners()
    }

    protected abstract fun init()

    protected abstract fun setupCollectors()

    protected abstract fun setupListeners()

    // Derived activity must wrap its main layout with a coordinator layout
    // and set this variable to the coordinator layout's view id, preferably in init() method
    protected lateinit var globalCoordinatorLayout: CoordinatorLayout

    open fun showSnackbar(message: String) {
        Snackbar.make(globalCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    // TODO: Can we use background job ?
    // Derived activity must include the progress_overlay layout in its main layout
    // Job may return a result string that will be shown on snackbar after complete
    open fun runJobShowProcessingOverlay(job: suspend () -> String?) = lifecycleScope.launch {
        whenStarted {
            showProcessingOverlay()
            val res = job()
            hideProcessingOverlay()
            res?.let { showSnackbar(it) }
        }
    }

    private fun showProcessingOverlay() {
        val progressOverlay: View = findViewById(R.id.progress_overlay)
        ActivityUtils.animateVisibility(progressOverlay, View.VISIBLE, 0.3f, 100)
    }

    private fun hideProcessingOverlay() {
        val progressOverlay: View = findViewById(R.id.progress_overlay)
        ActivityUtils.animateVisibility(progressOverlay, View.GONE, 0F, 100)
    }
}