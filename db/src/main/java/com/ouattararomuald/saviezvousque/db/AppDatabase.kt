package com.ouattararomuald.saviezvousque.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ouattararomuald.saviezvousque.db.daos.FeedCategoryDao
import com.ouattararomuald.saviezvousque.db.daos.FeedItemDao

@TypeConverters(DateTimeConverter::class)
@Database(entities = [
  FeedCategory::class,
  FeedItem::class
], version = 3)
abstract class AppDatabase : RoomDatabase() {
  abstract fun feedCategoryDao(): FeedCategoryDao

  abstract fun feedItemDao(): FeedItemDao
}