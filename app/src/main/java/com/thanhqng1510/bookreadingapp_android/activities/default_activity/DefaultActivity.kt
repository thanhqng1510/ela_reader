package com.thanhqng1510.bookreadingapp_android.activities.default_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.snackbar.Snackbar
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.ActivityUtils.animateVisibility
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.ActivityUtils.showSnackbar
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.BaseActivity
import kotlinx.coroutines.launch

abstract class DefaultActivity : AppCompatActivity(), BaseActivity {
    /**
     * Use CoordinatorLayout to provide fully support for snackbar and bottom navigation bar
     *
     * Derived activity must wrap its main layout with a coordinator layout and
     * set this variable to the coordinator layout's view id, preferably in setupBindings() method
     */
    protected lateinit var globalCoordinatorLayout: CoordinatorLayout

    /**
     * A loading overlay layout to support waitJobShowProgressOverlayAsync method
     *
     * Derived activity must include the progress_overlay layout in its main layout
     * and set this variable to R.id.progress_overlay, preferably in setupBindings() method
     */
    protected lateinit var progressOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBindings(savedInstanceState)
        setupCollectors()
        setupListeners()
    }

    override fun finishWithResult(resultCode: Int, data: Map<String, String>) {
        val intent = Intent()
        data.forEach { (k, v) -> intent.putExtra(k, v) }
        setResult(resultCode, intent)
        finish()
    }

    override fun showSnackbar(message: String) =
        globalCoordinatorLayout.showSnackbar(message, Snackbar.LENGTH_SHORT)

    // TODO: Can we use background job ?
    // Job may return a result string that will be shown on snackbar after complete
    override fun waitJobShowProgressOverlayAsync(job: suspend () -> String?) =
        lifecycleScope.launch {
            whenStarted {
                showProgressOverlay()
                val res = job()
                hideProgressOverlay()
                res?.let { showSnackbar(it) }
            }
        }

    private fun showProgressOverlay() =
        progressOverlay.animateVisibility(View.VISIBLE, 0.3f, 100)

    private fun hideProgressOverlay() =
        progressOverlay.animateVisibility(View.GONE, 0F, 100)
}