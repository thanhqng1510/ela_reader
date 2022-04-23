package com.thanhqng1510.bookreadingapp_android.models.entities.bookmark

import androidx.room.Embedded
import androidx.room.Relation
import com.thanhqng1510.bookreadingapp_android.models.UIModel
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book

data class BookmarkWithBook(
    @Embedded val bookmark: Bookmark,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "rowid"
    )
    val book: Book
) : UIModel<BookmarkWithBook> {
    override fun areItemsTheSame(other: BookmarkWithBook) =
        bookmark.id == other.bookmark.id

    override fun areContentsTheSame(other: BookmarkWithBook) =
        book.areContentsTheSame(other.book)
}