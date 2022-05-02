package com.thanhqng1510.ela_reader.models.entities.bookmark

import androidx.room.Embedded
import androidx.room.Relation
import com.thanhqng1510.ela_reader.models.UIModel
import com.thanhqng1510.ela_reader.models.entities.book.Book

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