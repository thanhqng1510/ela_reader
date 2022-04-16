package com.thanhqng1510.bookreadingapp_android.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        const val booksExternalDir = "books/"
    }

    override fun onCreate() {
        super.onCreate()
        initExternalFilesDir()
    }

    private fun initExternalFilesDir() {
        val booksDir =
            "${getExternalFilesDir(null)}/${booksExternalDir}"

        with(File(booksDir)) {
            if (!exists())
                mkdir()
        }
    }
}