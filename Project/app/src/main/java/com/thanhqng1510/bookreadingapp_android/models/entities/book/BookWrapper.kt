package com.thanhqng1510.bookreadingapp_android.models.entities.book

import androidx.annotation.NonNull
import androidx.room.*

@Fts4
@Entity(tableName = "books")
data class BookWrapper(
    @TypeConverters(BookWrapperConverter::class)
    @NonNull
    val data: Book
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var rowId: Int = 0
}