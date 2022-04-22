package com.thanhqng1510.bookreadingapp_android.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        const val externalBooksFolder = "books/"
    }

    lateinit var externalBooksDir: String

    override fun onCreate() {
        super.onCreate()
        externalBooksDir = "${getExternalFilesDir(null)}/${externalBooksFolder}"
        initExternalFilesDir()
    }

    private fun initExternalFilesDir() {
        externalBooksDir = "${getExternalFilesDir(null)}/${externalBooksFolder}"
        with(File(externalBooksDir)) {
            if (!exists())
                mkdir()
        }
    }
}