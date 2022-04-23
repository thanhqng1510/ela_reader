package com.thanhqng1510.bookreadingapp_android.utils.fragment_utils

import androidx.fragment.app.Fragment

/**
 * Represent an object that create and provide fragments when requested
 */
interface FragmentProvider {
    fun provideFragment(): Fragment
}