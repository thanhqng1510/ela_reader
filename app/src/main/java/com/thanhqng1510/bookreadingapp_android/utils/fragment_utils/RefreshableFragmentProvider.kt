package com.thanhqng1510.bookreadingapp_android.utils.fragment_utils

/**
 * Represent an object that create and provide fragments when requested
 *
 * Each fragment must associated with a unique tag string and support refresh data
 */
interface RefreshableFragmentProvider : FragmentProvider {
    override fun getFragment(): RefreshableBaseFragment
}