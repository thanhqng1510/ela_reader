package com.thanhqng1510.bookreadingapp_android.activities.settings

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.thanhqng1510.bookreadingapp_android.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupBindings()
        setupCallbacks()
    }

    private fun setupBindings() {
        backBtn = findViewById(R.id.back_btn)
    }

    private fun setupCallbacks() {
        backBtn.setOnClickListener { finish() }
    }
}