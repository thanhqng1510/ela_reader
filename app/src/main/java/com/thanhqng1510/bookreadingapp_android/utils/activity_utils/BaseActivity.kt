package com.thanhqng1510.bookreadingapp_android.utils.activity_utils

import android.os.Bundle
import kotlinx.coroutines.Job

/**
 * Base activity interface for a cleaner implementation
 */
interface BaseActivity {
    /**
     * Method to handle layout binding and initializing
     */
    fun setupBindings(savedInstanceState: Bundle?)

    /**
     * Method to handle flow collecting
     */
    fun setupCollectors()

    /**
     * Method to handle event callbacks setup
     */
    fun setupListeners()

    /**
     * Helper method to finish current activity with result
     */
    fun finishWithResult(resultCode: Int, data: Map<String, String>)

    /**
     * Helper method to show snackbar
     */
    fun showSnackbar(message: String)

    /**
     * Helper method to dim screen and add a loading overlay while waiting for a suspend job to complete
     */
    fun waitJobShowProgressOverlayAsync(job: suspend () -> String?): Job
}