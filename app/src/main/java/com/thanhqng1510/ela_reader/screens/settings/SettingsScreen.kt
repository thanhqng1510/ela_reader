package com.thanhqng1510.ela_reader.screens.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.databinding.SettingsScreenBinding
import com.thanhqng1510.ela_reader.screens.AppViewModel
import com.thanhqng1510.ela_reader.utils.fragment_utils.BaseFragment

class SettingsScreen : BaseFragment() {
    // Bindings
    private var bindings: SettingsScreenBinding? = null

    private val appViewModel: AppViewModel by activityViewModels()

    override fun setupView(inflater: LayoutInflater, container: ViewGroup?): View {
        bindings = SettingsScreenBinding.inflate(inflater, container, false)
        return bindings!!.root
    }

    override fun setupBindings() {
        childFragmentManager.beginTransaction()
            .replace(bindings!!.mainBody.id, SettingsSupportFragment()).commit()

        appViewModel.appBarTitle.value =
            requireContext().resources.getString(R.string.settings_screen_label)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {}

    override fun cleanUpView() {
        bindings = null
    }
}