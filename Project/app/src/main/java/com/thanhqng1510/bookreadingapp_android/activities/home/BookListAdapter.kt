package com.thanhqng1510.bookreadingapp_android.activities.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book

internal class BookListAdapter(private val bookList: List<Book>): RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
        val cover: ImageView = view.findViewById(R.id.cover)
        val status: ImageView = view.findViewById(R.id.book_status)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.book_list_row_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = bookList[position].title
        holder.author.text = bookList[position].authors.joinToString(", ")
        holder.cover.setImageResource(bookList[position].coverResId ?: R.mipmap.book_cover_default)

        if (bookList[position].status.eVal != Book.STATUS.FINISHED) {
            holder.status.setImageResource(
                when (bookList[position].status.eVal) {
                    Book.STATUS.NEW -> R.drawable.new_status_light
                    Book.STATUS.READING -> R.drawable.reading_status_light
                    else -> 0 // TODO: need enhancement
                }
            )
        }
    }

    override fun getItemCount(): Int = bookList.size

    enum class DATACHANGED {
        INSERT,
        REMOVE
    }

    fun onBookListDataChange(type: DATACHANGED, atIdx: Int, size: Int) {
        when (type) {
            DATACHANGED.INSERT -> notifyItemRangeInserted(atIdx, size)
            DATACHANGED.REMOVE -> notifyItemRangeRemoved(atIdx, size)
        }
    }
}