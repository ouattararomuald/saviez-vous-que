package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FeedDao {
  @Query("SELECT * FROM Feed")
  fun getAll(): Flowable<List<Feed>>

  @Query("SELECT * FROM Feed WHERE category = :category LIMIT 1")
  fun getFeedByCategory(category: String): Single<Feed>

  @Insert
  fun insert(feed: Feed)

  @Query("DELETE FROM Feed")
  fun deleteAll()
}