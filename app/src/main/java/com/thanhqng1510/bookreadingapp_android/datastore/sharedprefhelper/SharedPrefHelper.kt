package com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper

import android.content.Context
import android.content.SharedPreferences
import com.thanhqng1510.bookreadingapp_android.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefHelper @Inject constructor() {
    fun sharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.globalSharedPreferencesFileKey, Context.MODE_PRIVATE)
}