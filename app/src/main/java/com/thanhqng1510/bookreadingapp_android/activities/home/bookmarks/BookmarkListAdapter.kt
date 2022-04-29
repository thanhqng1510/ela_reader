package com.thanhqng1510.bookreadingapp_android.activities.home.bookmarks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.BookmarkWithBook
import com.thanhqng1510.bookreadingapp_android.utils.adapter_utils.ModelListAdapter
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FileUtils.isExist

class BookmarkListAdapter(private val context: Context) :
    ModelListAdapter<BookmarkWithBook, BookmarkListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookTitle: TextView = view.findViewById(R.id.book_title)
        val cover: ImageView = view.findViewById(R.id.cover)
        val page: TextView = view.findViewById(R.id.page)
        val status: ImageView = view.findViewById(R.id.bookmark_status)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.bookmark_list_row_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)

        holder.run {
            bookTitle.text = data.book.title
            page.text =
                context.getString(R.string.bookmark_page, data.bookmark.page, data.book.numPages)

            if (data.book.status == Book.BookStatus.ERROR)
                status.setImageResource(R.drawable.error_status_light)

            data.book.thumbnailUri.let {
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