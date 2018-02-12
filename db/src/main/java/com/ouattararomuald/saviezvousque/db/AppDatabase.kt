package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database


@Database(entities = [Feed::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
  abstract fun feedDao(): FeedDao
}