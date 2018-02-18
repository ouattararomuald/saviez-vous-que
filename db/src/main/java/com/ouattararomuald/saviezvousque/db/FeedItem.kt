package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(indices = [
  Index(value = ["imageUrl"], unique = true),
  Index(value = ["publishedOn"]),
  Index(value = ["updatedOn"])
])
data class FeedItem constructor(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val imageUrl: String,
    @ColumnInfo val publishedOn: OffsetDateTime,
    @ColumnInfo val updatedOn: OffsetDateTime,
    @ColumnInfo val content: String
)