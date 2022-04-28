package com.thanhqng1510.bookreadingapp_android.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.thanhqng1510.bookreadingapp_android.R

/**
 * Service to manage and play ambient sounds while reading books
 */
class AmbientSoundPlayerService : Service() {
    enum class AmbientSoundType(val resId: Int, val displayStr: String) {
        NONE(0, "None"),
        MELANCHOLIC(0, "Melancholic"),
        NOSTALGIC(0, "Nostalgic"),
        RELAXING(R.raw.when_the_love_falls, "Relaxing"),
        SOOTHING(0, "Soothing"),
        FOCUS(0, "Focus"),
        CINEMATIC(0, "Cinematic");

        companion object {
            fun fromStr(string: String) = values().find { ambient -> ambient.displayStr == string }
        }
    }

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