package com.thanhqng1510.bookreadingapp_android.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

object ActivityUtils {
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

    fun showSnackbar(coordinatorLayout: CoordinatorLayout, message: String, duration: Int) =
        Snackbar.make(coordinatorLayout, message, duration).show()
}