package com.thanhqng1510.bookreadingapp_android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.thanhqng1510.bookreadingapp_android.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBindings()
        setupCallbacks()
    }

    private fun setupBindings() {

    }

    private fun setupCallbacks() {
        val backButton : ImageButton = findViewById(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }
    }
}