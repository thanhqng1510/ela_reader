package com.thanhqng1510.bookreadingapp_android.activities.settings_activity

import android.os.Bundle
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.databinding.ActivitySettingsBinding
import com.thanhqng1510.bookreadingapp_android.utils.activity_utils.BaseActivity

class SettingsActivity : BaseActivity() {
    private lateinit var bindings: ActivitySettingsBinding

    override fun setupBindings(savedInstanceState: Bundle?) {
        bindings = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        globalCoordinatorLayout = bindings.coordinatorLayout
        progressOverlay = findViewById(R.id.progress_overlay)

        supportFragmentManager.beginTransaction().replace(bindings.mainBody.id, SettingsFragment())
            .commit()
    }

    override fun setupCollectors() {}

    override fun setupListeners() =
        bindings.backBtn.setOnClickListener { finish() }
}