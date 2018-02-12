package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


@Entity(indices = [
  Index(value = ["category"], unique = true)
])
data class Feed constructor(
    @ColumnInfo val category: String,
    @ColumnInfo val xml: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)