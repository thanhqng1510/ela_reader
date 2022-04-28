package com.thanhqng1510.bookreadingapp_android.utils.fragment_utils

import androidx.fragment.app.Fragment

/**
 * Represent an object that create and provide fragments when requested
 *
 * Each fragment must associated with a unique tag string
 */
interface FragmentProvider {
    fun getFragment(): Fragment

    fun getLayoutResourceId(): Int

    fun getTag(): String
}