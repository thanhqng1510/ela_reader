package com.thanhqng1510.bookreadingapp_android.datamodels.entities

import androidx.recyclerview.widget.DiffUtil

class BookDiffCallBack : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.authors == newItem.authors &&
                oldItem.coverResId == newItem.coverResId &&
                oldItem.status == newItem.status
    }
}