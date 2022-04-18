package com.thanhqng1510.bookreadingapp_android.activities.settings

import android.os.Bundle
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity
import com.thanhqng1510.bookreadingapp_android.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {
    private lateinit var bindings: ActivitySettingsBinding

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        bindings.backBtn.setOnClickListener { finish() }
    }
}