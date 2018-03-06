package com.ouattararomuald.saviezvousque.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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