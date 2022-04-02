package com.thanhqng1510.bookreadingapp_android.activities.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SortOptionSpinnerAdapter(context: Context, resource: Int, items: List<String>)
    : ArrayAdapter<String>(context, resource, items) {
    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).
                                  inflate(android.R.layout.simple_spinner_dropdown_item, container, false)

        (view.findViewById(android.R.id.text1) as TextView).text = ""
        return view
    }
}