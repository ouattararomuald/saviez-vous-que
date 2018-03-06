package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface FeedItemDao {
  @Query("SELECT * FROM FeedItem ORDER BY updatedOn, publishedOn DESC")
  fun getAll(): Flowable<List<FeedItem>>

  @Query("SELECT * FROM FeedItem WHERE categoryId = :categoryId ORDER BY updatedOn, publishedOn DESC")
  fun getAllByCategory(categoryId: Int): Flowable<List<FeedItem>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(feedItems: List<FeedItem>)

  @Query("DELETE FROM FeedItem")
  fun deleteAll()
}