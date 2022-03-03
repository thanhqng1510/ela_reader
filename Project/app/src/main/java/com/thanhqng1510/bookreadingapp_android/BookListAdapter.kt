package com.thanhqng1510.bookreadingapp_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.thanhqng1510.bookreadingapp_android.models.Book

class BookListAdapter(private val context: Context, private val rowLayoutId: Int, private val bookList: List<Book>) : BaseAdapter() {
    override fun getCount(): Int = bookList.size

    override fun getItem(p0: Int): Any = bookList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context ?: context).inflate(R.layout.book_list_row_layout, parent, false)
        val title = view.findViewById<TextView>(R.id.title)
        val author = view.findViewById<TextView>(R.id.author)
        val cover = view.findViewById<ImageView>(R.id.cover)

        title.text = bookList[position].title
        author.text = bookList[position].authors.joinToString(", ")
        cover.setImageResource(bookList[position].coverId ?: R.mipmap.book_cover_default)

        return view
    }
}