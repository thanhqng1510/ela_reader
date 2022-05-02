package com.thanhqng1510.ela_reader.utils.fragment_utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.Job

/**
 * Base fragment interface for a cleaner implementation
 */
interface EasyFragment {
    /**
     * Method to handle view creating
     */
    fun setupView(inflater: LayoutInflater, container: ViewGroup?): View?

    /**
     * Method to handle layout binding and initializing
     */
    fun setupBindings()

    /**
     * Method to handle flow collecting
     */
    fun setupCollectors()

    /**
     * Method to handle event callbacks setup
     */
    fun setupListeners()

    /**
     * Method to handle view destroying
     */
    fun cleanUpView()

    /**
     * Helper method to show snackbar
     */
    fun showSnackbar(message: String)

    /**
     * Helper method to dim screen and add a loading overlay while waiting for a suspend job to complete
     */
    fun waitJobShowProgressOverlayAsync(job: suspend () -> String?): Job
}