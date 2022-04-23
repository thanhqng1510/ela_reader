package com.thanhqng1510.bookreadingapp_android.models.entities.book

import androidx.room.Embedded
import androidx.room.Relation
import com.thanhqng1510.bookreadingapp_android.models.UIModel
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.Bookmark

data class BookWithBookmarks(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "rowid",
        entityColumn = "bookId"
    )
    val bookmarks: List<Bookmark>
) : UIModel<BookWithBookmarks> {
    override fun areItemsTheSame(other: BookWithBookmarks) = book.areItemsTheSame(other.book)

    override fun areContentsTheSame(other: BookWithBookmarks) =
        book.areContentsTheSame(other.book)
}