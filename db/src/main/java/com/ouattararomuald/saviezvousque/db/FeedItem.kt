package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDateTime


@Entity(indices = [
  Index(value = ["imageUrl"], unique = true),
  Index(value = ["publishedOn"]),
  Index(value = ["updatedOn"])
])
data class FeedItem constructor(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val imageUrl: String,
    @ColumnInfo val publishedOn: LocalDateTime,
    @ColumnInfo val updatedOn: LocalDateTime,
    @ColumnInfo val content: String
)