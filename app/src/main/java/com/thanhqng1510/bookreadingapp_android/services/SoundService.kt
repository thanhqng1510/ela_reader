package com.thanhqng1510.bookreadingapp_android.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class SoundService : Service() {
    companion object {
        const val rawResIdExtra = "rawResIdExtra"
    }

    private lateinit var player: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val resId = intent?.getIntExtra(rawResIdExtra, -1) ?: -1
        if (resId == -1)
            stopSelf()

        player = MediaPlayer.create(this, resId)
        player.isLooping = true
        player.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        stopSelf()
        super.onDestroy()
    }
}