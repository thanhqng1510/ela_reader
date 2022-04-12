package com.thanhqng1510.bookreadingapp_android.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import java.util.*


fun String.formatForFileName(): String {
    return this.substringBeforeLast(".").trim().replace("""\s+""".toRegex(), " ").split(" ")
        .joinToString(separator = " ") {
            it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
            }
        }
}

object AndroidUtils {
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
}