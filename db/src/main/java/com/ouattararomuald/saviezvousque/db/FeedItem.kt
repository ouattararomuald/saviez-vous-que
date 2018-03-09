package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import org.threeten.bp.LocalDateTime


@Entity(primaryKeys = ["id", "categoryId"],
    indices = [
  Index(value = ["imageUrl", "categoryId"], unique = true),
  Index(value = ["publishedOn"]),
  Index(value = ["updatedOn"])
])
data class FeedItem constructor(
    val id: Int,
    val categoryId: Int,
    @ColumnInfo val imageUrl: String?,
    @ColumnInfo val publishedOn: LocalDateTime,
    @ColumnInfo val updatedOn: LocalDateTime,
    @ColumnInfo val title: String,
    @ColumnInfo val content: String
)