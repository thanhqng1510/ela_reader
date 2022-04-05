package com.thanhqng1510.bookreadingapp_android.datamodels.entities

import androidx.room.*

@Fts4
@Entity(tableName="books")
data class BookWrapper(
    @TypeConverters(BookWrapperConverter::class) val data: Book
) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var rowId: Int = 0
}