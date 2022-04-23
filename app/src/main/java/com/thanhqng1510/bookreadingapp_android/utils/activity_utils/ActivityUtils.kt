package com.thanhqng1510.bookreadingapp_android.utils.activity_utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

object ActivityUtils {
    fun View.animateVisibility(destVisibility: Int, destAlpha: Float, durationMillis: Long) {
        val toShow = destVisibility == View.VISIBLE
        if (toShow)
            alpha = 0F

        visibility = View.VISIBLE
        animate()
            .setDuration(durationMillis)
            .alpha(if (toShow) destAlpha else 0F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = destVisibility
                }
            })
    }

    fun CoordinatorLayout.showSnackbar(message: String, duration: Int) =
        Snackbar.make(this, message, duration).show()
}