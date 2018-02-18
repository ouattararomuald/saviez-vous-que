package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = [
  Index(value = ["numberOfItems"])
])
internal data class FeedCategory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val numberOfItems: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val slug: String
)