package com.thanhqng1510.bookreadingapp_android.activities.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.snackbar.Snackbar
import com.thanhqng1510.bookreadingapp_android.utils.ActivityUtils
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    // Derived activity must wrap its main layout with a coordinator layout
    // and set this variable to the coordinator layout's view id, preferably in setupBindings() method
    protected lateinit var globalCoordinatorLayout: CoordinatorLayout

    // Derived activity must include the progress_overlay layout in its main layout
    // and set this variable to R.id.progress_overlay, preferably in setupBindings() method
    protected lateinit var progressOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBindings(savedInstanceState)
        setupCollectors()
        setupListeners()
    }

    protected abstract fun setupBindings(savedInstanceState: Bundle?)

    protected abstract fun setupCollectors()

    protected abstract fun setupListeners()

    open fun finishWithResult(resultCode: Int, data: Map<String, String>) {
        val intent = Intent()
        data.forEach { (k, v) -> intent.putExtra(k, v) }
        setResult(resultCode, intent)
        finish()
    }

    open fun showSnackbar(message: String) {
        Snackbar.make(globalCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    // TODO: Can we use background job ?
    // Job may return a result string that will be shown on snackbar after complete
    open fun waitJobShowProcessingOverlayAsync(job: suspend () -> String?) = lifecycleScope.launch {
        whenStarted {
            showProcessingOverlay()
            val res = job()
            hideProcessingOverlay()
            res?.let { showSnackbar(it) }
        }
    }

    private fun showProcessingOverlay() {
        ActivityUtils.animateVisibility(progressOverlay, View.VISIBLE, 0.3f, 100)
    }

    private fun hideProcessingOverlay() {
        ActivityUtils.animateVisibility(progressOverlay, View.GONE, 0F, 100)
    }
}