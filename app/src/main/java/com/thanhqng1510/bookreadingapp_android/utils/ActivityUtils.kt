package com.thanhqng1510.bookreadingapp_android.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.media.SoundPool
import android.view.View
import com.thanhqng1510.bookreadingapp_android.R

object ActivityUtils {
    enum class AMBIENT(val resid: Int, val displayStr: String) {
        RANDOM(0, "Random"),
        RAIN(R.raw.rain, "Rain"),
        STORM(R.raw.storm, "Storm"),
        WAVES(R.raw.waves, "Waves"),
        RIVER(0, "River"),
        COFFEE_SHOP(0, "Coffee shop"),
        FIRE(0, "Fire"),
        NIGHT(0, "Night"),
        WIND(0, "Wind");

        companion object {
            fun fromStr(string: String): AMBIENT =
                values().find { ambient -> ambient.displayStr == string } ?: default()

            fun default(): AMBIENT = RANDOM
        }
    }

    private var ambientPlayer: SoundPool? = null

    fun animateVisibility(view: View, destVisibility: Int, destAlpha: Float, durationMillis: Long) {
        val toShow = destVisibility == View.VISIBLE
        if (toShow) {
            view.alpha = 0F
        }

        view.visibility = View.VISIBLE
        view.animate()
            .setDuration(durationMillis)
            .alpha(if (toShow) destAlpha else 0F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = destVisibility
                }
            })
    }

    fun playAudio(context: Context, resid: Int) {
        if (ambientPlayer == null)
            ambientPlayer = SoundPool.Builder().setMaxStreams(AMBIENT.values().size - 1).build()

        ambientPlayer?.let { player ->
            player.setOnLoadCompleteListener { player, sampleId, _ ->
                player.play(sampleId, 1.0f, 1.0f, 1, -1, 1.0f)
            }
            player.load(context, resid, 1)
        }
    }

    fun stopAudio() {
        ambientPlayer?.release()
        ambientPlayer = null
    }
}