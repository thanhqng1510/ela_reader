package com.thanhqng1510.bookreadingapp_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.models.Book

class BookListAdapter(private val context: Context, private val bookList: List<Book>) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
        val cover: ImageView = view.findViewById(R.id.cover)
        val status: ImageView = view.findViewById(R.id.book_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context ?: context).inflate(R.layout.book_list_row_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = bookList[position].title
        holder.author.text = bookList[position].authors.joinToString(", ")
        holder.cover.setImageResource(bookList[position].coverId ?: R.mipmap.book_cover_default)

        if (bookList[position].status != Book.STATUS.FINISHED) {
            holder.status.setImageResource(
                when (bookList[position].status) {
                    Book.STATUS.NEW -> R.drawable.new_status_light
                    Book.STATUS.READING -> R.drawable.reading_status_light
                    else -> 0
                }
            )
        }
    }

    override fun getItemCount(): Int = bookList.size
}