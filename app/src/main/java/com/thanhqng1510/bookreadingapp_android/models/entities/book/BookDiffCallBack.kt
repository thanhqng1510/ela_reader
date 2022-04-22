package com.thanhqng1510.bookreadingapp_android.models.entities.book

import androidx.recyclerview.widget.DiffUtil

class BookDiffCallBack : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem
}