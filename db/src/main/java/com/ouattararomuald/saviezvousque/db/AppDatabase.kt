package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters


@TypeConverters(DateTimeConverter::class)
@Database(entities = [
  FeedCategory::class,
  FeedItem::class
], version = 2)
abstract class AppDatabase : RoomDatabase() {
  abstract fun feedCategoryDao(): FeedCategoryDao

  abstract fun feedItemDao(): FeedItemDao
}