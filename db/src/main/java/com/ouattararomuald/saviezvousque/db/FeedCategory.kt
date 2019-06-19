package com.ouattararomuald.saviezvousque.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [
  Index(value = ["numberOfItems"])
])
data class FeedCategory(
  @PrimaryKey(autoGenerate = true) val id: Int,
  val numberOfItems: Int,
  val name: String,
  val slug: String
)