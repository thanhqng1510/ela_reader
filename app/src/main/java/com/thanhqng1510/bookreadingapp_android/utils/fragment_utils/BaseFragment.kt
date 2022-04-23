package com.thanhqng1510.bookreadingapp_android.utils.fragment_utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Base fragment interface for a cleaner implementation
 */
interface BaseFragment {
    /**
     * Method to handle view creating
     */
    fun setupView(inflater: LayoutInflater, container: ViewGroup?): View?

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
     * Method to handle view destroying
     */
    fun cleanUpView()
}