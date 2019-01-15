package com.ouattararomuald.saviezvousque.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import org.threeten.bp.LocalDateTime

@Entity(primaryKeys = ["id", "categoryId"],
    indices = [
      Index(value = ["imageUrl", "categoryId"], unique = true),
      Index(value = ["publishedOn"]),
      Index(value = ["updatedOn"])
    ])
data class FeedItem(
  val id: Int,
  val categoryId: Int,
  @ColumnInfo val imageUrl: String?,
  @ColumnInfo val publishedOn: LocalDateTime,
  @ColumnInfo val updatedOn: LocalDateTime,
  @ColumnInfo val title: String,
  @ColumnInfo val content: String
)