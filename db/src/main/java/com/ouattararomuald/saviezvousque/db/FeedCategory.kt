package com.ouattararomuald.saviezvousque.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [
  Index(value = ["numberOfItems"])
])
data class FeedCategory(
  @PrimaryKey(autoGenerate = true) val id: Int,
  @ColumnInfo val numberOfItems: Int,
  @ColumnInfo val name: String,
  @ColumnInfo val slug: String
)