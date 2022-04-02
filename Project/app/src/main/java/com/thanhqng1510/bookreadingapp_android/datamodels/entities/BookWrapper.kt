package com.thanhqng1510.bookreadingapp_android.datamodels.entities

import androidx.room.*

@Fts4
@Entity(tableName = "books")
class BookWrapper(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowId: Int,
    @TypeConverters(BookWrapperConverter::class) val data: Book
)