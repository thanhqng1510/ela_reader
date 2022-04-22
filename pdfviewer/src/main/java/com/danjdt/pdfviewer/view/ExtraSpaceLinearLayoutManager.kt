package com.danjdt.pdfviewer.view

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danjdt.pdfviewer.utils.Utils

class ExtraSpaceLinearLayoutManager(private val context: Context?) : LinearLayoutManager(context) {
    @Deprecated("Deprecated in Java")
    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return Utils.getScreenHeight(context as Activity)
    }
}