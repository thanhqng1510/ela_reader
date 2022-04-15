package com.thanhqng1510.bookreadingapp_android.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        const val booksExternalDir = "books/"

        const val contentSchemeUri = "content"
        const val fileSchemeUri = "file"
    }
}