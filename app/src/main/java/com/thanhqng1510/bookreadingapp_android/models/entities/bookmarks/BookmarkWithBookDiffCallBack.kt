package com.thanhqng1510.bookreadingapp_android.models.entities.bookmarks

import androidx.recyclerview.widget.DiffUtil

class BookmarkWithBookDiffCallBack : DiffUtil.ItemCallback<Bookmark.BookmarkWithBook>() {
    override fun areItemsTheSame(
        oldItem: Bookmark.BookmarkWithBook,
        newItem: Bookmark.BookmarkWithBook
    ): Boolean =
        oldItem.bookmark.id == newItem.bookmark.id

    override fun areContentsTheSame(
        oldItem: Bookmark.BookmarkWithBook,
        newItem: Bookmark.BookmarkWithBook
    ): Boolean =
        oldItem == newItem
}