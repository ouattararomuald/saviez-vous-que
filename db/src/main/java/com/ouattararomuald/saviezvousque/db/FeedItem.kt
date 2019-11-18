package com.ouattararomuald.saviezvousque.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import org.threeten.bp.LocalDateTime

@Entity(
  primaryKeys = ["id", "categoryId"],
  foreignKeys = [
    ForeignKey(
        entity = FeedCategory::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"]
    )
  ],
  indices = [
    Index(value = ["imageUrl", "categoryId"], unique = true),
    Index(value = ["categoryId"]),
    Index(value = ["publishedOn"]),
    Index(value = ["updatedOn"])
  ]
)
data class FeedItem(
  val id: Int,
  val categoryId: Int,
  val imageUrl: String?,
  val publishedOn: LocalDateTime,
  val updatedOn: LocalDateTime,
  val title: String,
  val content: String
)