package com.thanhqng1510.bookreadingapp_android.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.utils.AndroidUtils

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupCollectors()
        setupListeners()
    }

    abstract fun init()

    abstract fun setupCollectors()

    abstract fun setupListeners()

    // Derived activity must wrap its main layout with a coordinator layout
    // and pass the coordinator layout as the first argument
    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    // Derived activity must include the progress_overlay layout in its main layout
    fun showLoadingOverlay() {
        val progressOverlay: View = findViewById(R.id.progress_overlay)
        AndroidUtils.animateVisibility(progressOverlay, View.VISIBLE, 0.3f, 100)
    }

    // Derived activity must include the progress_overlay layout in its main layout
    fun hideLoadingOverlay() {
        val progressOverlay: View = findViewById(R.id.progress_overlay)
        AndroidUtils.animateVisibility(progressOverlay, View.GONE, 0F, 100)
    }
}