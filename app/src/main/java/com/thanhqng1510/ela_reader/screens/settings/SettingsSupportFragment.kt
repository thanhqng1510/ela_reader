package com.thanhqng1510.ela_reader.screens.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.thanhqng1510.ela_reader.R

class SettingsSupportFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
        setPreferencesFromResource(R.xml.settings_layout, rootKey)
}