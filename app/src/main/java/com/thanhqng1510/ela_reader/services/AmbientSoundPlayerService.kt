package com.thanhqng1510.ela_reader.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.thanhqng1510.ela_reader.R

/**
 * Service to manage and play ambient sounds while reading books
 */
class AmbientSoundPlayerService : Service() {
    enum class AmbientSoundType(val resIds: IntArray, val displayStr: String) {
        NONE(intArrayOf(), "None"),
        MELANCHOLIC(intArrayOf(R.raw.gnossienne_4, R.raw.consolation_3), "Melancholic"),
        NOSTALGIC(
            intArrayOf(
                R.raw.a_time_of_wonder,
                R.raw.waltz_op_34_no_2,
                R.raw.nocturne_op_9_no_1
            ), "Nostalgic"
        ),
        RELAXING(
            intArrayOf(R.raw.sleep_away, R.raw.nocturne_op_9_no_2, R.raw.when_the_love_falls),
            "Relaxing"
        ),
        SOOTHING(intArrayOf(R.raw.le_cygne, R.raw.nocturne_op_55_no_1, R.raw.reverie), "Soothing"),
        FOCUS(intArrayOf(), "Focus"),
        CINEMATIC(intArrayOf(), "Cinematic");

        companion object {
            fun fromStr(string: String) = values().find { ambient -> ambient.displayStr == string }
        }
    }

    companion object {
        const val arrayRawResIdExtra = "arrayRawResIdExtra"
    }

    private lateinit var player: MediaPlayer

    private var currentSongIdx = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val resIds = intent?.getIntArrayExtra(arrayRawResIdExtra) ?: run {
            stopSelf()
            return START_NOT_STICKY
        }

        player = createAmbientPlayer(resIds)
        player.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        stopSelf()
        super.onDestroy()
    }

    private fun createAmbientPlayer(resIds: IntArray): MediaPlayer {
        var player = MediaPlayer.create(this, resIds[currentSongIdx])
        player.setOnCompletionListener {
            currentSongIdx = (currentSongIdx + 1) % resIds.size
            player = createAmbientPlayer(resIds)
            player.start()
        }

        return player
    }
}