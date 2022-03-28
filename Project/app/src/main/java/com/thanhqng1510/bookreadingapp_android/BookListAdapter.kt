package com.thanhqng1510.bookreadingapp_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.models.Book

class BookListAdapter(private val bookList: List<Book>) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val author: TextView
        val cover: ImageView
        val status: ImageView

        init {
            title = view.findViewById(R.id.title)
            author = view.findViewById(R.id.author)
            cover = view.findViewById(R.id.cover)
            status = view.findViewById(R.id.book_status)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.book_list_row_layout, viewGroup, false)
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