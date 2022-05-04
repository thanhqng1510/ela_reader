package com.thanhqng1510.ela_reader.application

// TODO: Fix screen title says 'Bookmarks' instead of 'Library' after adding a book.

import android.app.Application
import com.thanhqng1510.ela_reader.utils.constant_utils.ConstantUtils
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class MainApplication @Inject constructor() : Application() {
    lateinit var externalBooksDir: String

    override fun onCreate() {
        super.onCreate()
        externalBooksDir = "${getExternalFilesDir(null)}/${ConstantUtils.externalBooksFolder}"
        initExternalFilesDir()
    }

    private fun initExternalFilesDir() {
        externalBooksDir = "${getExternalFilesDir(null)}/${ConstantUtils.externalBooksFolder}"
        with(File(externalBooksDir)) {
            if (!exists())
                mkdir()
        }
    }
}