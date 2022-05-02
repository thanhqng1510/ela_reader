package com.thanhqng1510.ela_reader.utils.fragment_utils

import androidx.fragment.app.Fragment

/**
 * Represent an object that create and provide fragments when requested
 *
 * Each fragment must associated with a unique tag string
 */
interface FragmentProvider {
    fun getFragment(): Fragment

    fun getTag(): String
}