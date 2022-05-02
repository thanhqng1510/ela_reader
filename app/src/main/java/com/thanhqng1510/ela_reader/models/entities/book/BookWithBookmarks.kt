package com.thanhqng1510.ela_reader.models.entities.book

import androidx.room.Embedded
import androidx.room.Relation
import com.thanhqng1510.ela_reader.models.UIModel
import com.thanhqng1510.ela_reader.models.entities.bookmark.Bookmark

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