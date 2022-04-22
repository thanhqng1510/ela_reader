package com.thanhqng1510.bookreadingapp_android.activities.bookmark

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.thanhqng1510.bookreadingapp_android.R

internal class BookmarkListSortOptionSpinnerAdapter(
    private val master: Spinner,
    private val resource: Int,
    private val items: List<String>,
    context: Context
) :
    ArrayAdapter<String>(context, resource, items) {
    enum class SORTBY(val displayStr: String) {
        DATE_ADDED("Date added"),
        TITLE("Title");

        companion object {
            fun forIndex(idx: Int): SORTBY = values().run { this[idx.coerceIn(0, this.size - 1)] }

            fun default(): SORTBY = DATE_ADDED
        }
    }

    private var dropdownResource = android.R.layout.simple_spinner_dropdown_item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        (view.findViewById(android.R.id.text1) as TextView).text = ""
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(dropdownResource, parent, false)
        (view.findViewById(R.id.text) as CheckedTextView).text = getItem(position)

        if (master.selectedItemPosition == position)
            (view.findViewById(R.id.tick) as ImageView).imageAlpha = 255 // Make tick image visible
        else
            (view.findViewById(R.id.tick) as ImageView).imageAlpha = 0 // Make tick image invisible

        return view
    }

    override fun setDropDownViewResource(resource: Int) {
        super.setDropDownViewResource(resource)
        dropdownResource = resource
    }

    override fun getItem(position: Int) = items[position]

    override fun getCount() = items.size

    override fun getItemId(position: Int) = position.toLong()
}