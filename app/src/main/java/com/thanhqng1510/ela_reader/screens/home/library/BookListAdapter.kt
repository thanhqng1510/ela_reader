package com.thanhqng1510.ela_reader.screens.home.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.ela_reader.R
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.utils.adapter_utils.ModelListAdapter
import com.thanhqng1510.ela_reader.utils.file_utils.FileUtils.isExist

class BookListAdapter : ModelListAdapter<Book, BookListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val author: TextView = view.findViewById(R.id.author)
        val cover: ImageView = view.findViewById(R.id.cover)
        val status: ImageView = view.findViewById(R.id.book_status)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.book_list_row_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)

        holder.run {
            title.text = book.title
            author.text = book.authors.joinToString(", ")

            when (book.status) {
                Book.BookStatus.NEW -> status.setImageResource(R.drawable.new_status_light)
                Book.BookStatus.READING -> status.setImageResource(R.drawable.reading_status_light)
                Book.BookStatus.ERROR -> status.setImageResource(R.drawable.error_status_light)
                else -> {}
            }

            book.thumbnailUri.let {
                if (it.isExist())
                    cover.setImageURI(it)
                else
                    cover.setImageResource(R.mipmap.book_cover_default)
            }

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(
                    it,
                    holder.adapterPosition
                )
            }
            itemView.setOnLongClickListener {
                longClickedPos = adapterPosition
                false
            }
        }
    }
}