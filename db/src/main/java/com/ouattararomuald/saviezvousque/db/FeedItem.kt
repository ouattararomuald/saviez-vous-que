package com.ouattararomuald.saviezvousque.db

import org.threeten.bp.LocalDateTime

data class FeedItem(
  val id: Int,
  val categoryId: Int,
  val imageUrl: String?,
  val publishedOn: LocalDateTime,
  val updatedOn: LocalDateTime,
  val title: String,
  val content: String
)