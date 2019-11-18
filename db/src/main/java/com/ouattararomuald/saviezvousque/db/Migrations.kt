@file:JvmName("MigrationsUtil")
package com.ouattararomuald.saviezvousque.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(database: SupportSQLiteDatabase) {
    //database.execSQL("ALTER TABLE FeedItem ADD CONSTRAINT fk_category_feed_item FOREIGN KEY(categoryId) REFERENCES FeedCategory(id)")
    //database.execSQL("ALTER TABLE FeedItem ADD INDEX category_id_index('categoryId')")
  }
}
