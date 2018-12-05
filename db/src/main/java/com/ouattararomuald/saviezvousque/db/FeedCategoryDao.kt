package com.ouattararomuald.saviezvousque.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface FeedCategoryDao {
  @Query("SELECT * FROM FeedCategory ORDER BY name ASC")
  fun getAll(): Flowable<List<FeedCategory>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(feedCategories: List<FeedCategory>)

  @Query("DELETE FROM FeedCategory")
  fun deleteAll()
}