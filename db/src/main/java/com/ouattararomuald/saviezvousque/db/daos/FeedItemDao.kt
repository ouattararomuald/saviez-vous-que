package com.ouattararomuald.saviezvousque.db.daos

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ouattararomuald.saviezvousque.db.FeedItem
import io.reactivex.Flowable

@Dao
interface FeedItemDao {
  @Query(
      "SELECT * FROM FeedItem WHERE categoryId = :categoryId ORDER BY updatedOn, publishedOn ASC")
  fun getItems(categoryId: Int): DataSource.Factory<Int, FeedItem>

  @Query("SELECT * FROM FeedItem ORDER BY updatedOn, publishedOn DESC")
  fun feedItemsStream(): Flowable<List<FeedItem>>

  @Query("SELECT * FROM FeedItem ORDER BY updatedOn, publishedOn DESC LIMIT 10")
  fun recentFeedItemsStream(): Flowable<List<FeedItem>>

  @Query(
      "SELECT * FROM FeedItem WHERE categoryId = :categoryId ORDER BY updatedOn, publishedOn DESC")
  fun feedItemsByCategoryStream(categoryId: Int): Flowable<List<FeedItem>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(feedItems: List<FeedItem>)

  @Query("DELETE FROM FeedItem")
  fun deleteAll()

  @Query("DELETE FROM FeedItem WHERE categoryId = :categoryId AND id in (:itemsToDelete)")
  fun deleteByCategory(categoryId: Int, itemsToDelete: List<Int>)

  @Transaction
  fun addPosts(categoryId: Int, itemsToDelete: List<Int>, feedItems: List<FeedItem>) {
    //deleteByCategory(categoryId, itemsToDelete)
    insert(feedItems)
  }
}