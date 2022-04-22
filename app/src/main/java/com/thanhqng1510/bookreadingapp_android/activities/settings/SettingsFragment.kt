package com.thanhqng1510.bookreadingapp_android.activities.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.thanhqng1510.bookreadingapp_android.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey)
    }
}