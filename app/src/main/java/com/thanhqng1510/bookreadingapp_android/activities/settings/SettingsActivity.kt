package com.thanhqng1510.bookreadingapp_android.activities.settings

import android.widget.ImageButton
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.activities.base.BaseActivity

class SettingsActivity : BaseActivity() {
    private lateinit var backBtn: ImageButton

    override fun init() {
        setContentView(R.layout.activity_settings)

        globalCoordinatorLayout = findViewById(R.id.coordinator_layout)
        backBtn = findViewById(R.id.back_btn)
    }

    override fun setupCollectors() {}

    override fun setupListeners() {
        backBtn.setOnClickListener { finish() }
    }
}