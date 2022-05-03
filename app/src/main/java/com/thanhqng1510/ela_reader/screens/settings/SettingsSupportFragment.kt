package com.thanhqng1510.ela_reader.screens.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.thanhqng1510.bookreadingapp_android.activities.settings_activity.TimePickerFragment
import com.thanhqng1510.ela_reader.R

class SettingsSupportFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_layout, rootKey)

        val alarm: Preference? = findPreference("alarm_setter")
        alarm?.setOnPreferenceClickListener {
            TimePickerFragment().show(parentFragmentManager, "timePicker")
            true
        }
    }
}