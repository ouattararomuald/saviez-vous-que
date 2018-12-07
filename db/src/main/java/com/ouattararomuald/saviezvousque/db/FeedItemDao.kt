package com.ouattararomuald.saviezvousque.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Flowable

@Dao
interface FeedItemDao {
  @Query(
      "SELECT * FROM FeedItem WHERE categoryId = :categoryId ORDER BY updatedOn, publishedOn ASC")
  fun getItems(categoryId: Int): DataSource.Factory<Int, FeedItem>

  @Query("SELECT * FROM FeedItem ORDER BY updatedOn, publishedOn DESC")
  fun getAll(): Flowable<List<FeedItem>>

  @Query(
      "SELECT * FROM FeedItem WHERE categoryId = :categoryId ORDER BY updatedOn, publishedOn DESC")
  fun getAllByCategory(categoryId: Int): Flowable<List<FeedItem>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(feedItems: List<FeedItem>)

  @Query("DELETE FROM FeedItem")
  fun deleteAll()

  @Query("DELETE FROM FeedItem WHERE categoryId = :categoryId")
  fun deleteByCategory(categoryId: Int)

  @Transaction
  fun addPosts(categoryId: Int, feedItems: List<FeedItem>) {
    deleteByCategory(categoryId)
    insert(feedItems)
  }
}