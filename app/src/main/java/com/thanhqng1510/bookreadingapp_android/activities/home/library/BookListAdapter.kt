package com.thanhqng1510.bookreadingapp_android.activities.home.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.R
import com.thanhqng1510.bookreadingapp_android.models.ModelDiffCallback
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.utils.file_utils.FileUtils.isExist
import com.thanhqng1510.bookreadingapp_android.utils.listener_utils.IOnItemClickAdapterPositionListener

class BookListAdapter : ListAdapter<Book, BookListAdapter.ViewHolder>(
    AsyncDifferConfig.Builder(ModelDiffCallback<Book>()).build()
) {
    var longClickedPos: Int? = null
        private set

    lateinit var onItemClickListener: IOnItemClickAdapterPositionListener

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
                Book.STATUS.NEW -> status.setImageResource(R.drawable.new_status_light)
                Book.STATUS.READING -> status.setImageResource(R.drawable.reading_status_light)
                Book.STATUS.ERROR -> status.setImageResource(R.drawable.error_status_light)
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