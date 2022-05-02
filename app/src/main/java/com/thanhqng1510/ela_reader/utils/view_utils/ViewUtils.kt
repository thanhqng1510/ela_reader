package com.thanhqng1510.ela_reader.utils.view_utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

object ViewUtils {
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

    fun CoordinatorLayout.showSnackbar(message: String, duration: Int, anchor: View?) {
        val snackBar = Snackbar.make(this, message, duration)
        anchor?.let { snackBar.anchorView = it }
        snackBar.show()
    }
}