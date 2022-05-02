package com.thanhqng1510.ela_reader.datastore.sharedprefhelper

import android.content.Context
import android.content.SharedPreferences
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefHelper @Inject constructor() {
    fun sharedPref(context: Context): SharedPreferences = context.getSharedPreferences(
        ConstantUtils.globalSharedPreferencesFileKey,
        Context.MODE_PRIVATE
    )
}