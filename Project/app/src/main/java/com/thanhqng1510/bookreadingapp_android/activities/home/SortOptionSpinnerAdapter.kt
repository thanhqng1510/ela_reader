package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

internal class SortOptionSpinnerAdapter(private val resource: Int, context: Context, items: List<String>) : ArrayAdapter<String>(context, resource, items) {
    enum class SORTBY(val dispString: String) {
        LAST_READ("Last read"),
        DATE_ADDED("Date added"),
        TITLE("Title");

        companion object {
            fun forIndex(idx: Int): SORTBY = values().run { this[idx.coerceIn(0, this.size - 1)] }
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, container, false)
        (view.findViewById(android.R.id.text1) as TextView).text = ""
        return view
    }
}