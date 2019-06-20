package com.ouattararomuald.saviezvousque.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ouattararomuald.saviezvousque.db.FeedCategory
import io.reactivex.Flowable

@Dao
abstract class FeedCategoryDao {
  @Query("SELECT * FROM FeedCategory ORDER BY name ASC")
  abstract fun feedCategoriesStream(): Flowable<List<FeedCategory>>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract fun insert(feedCategories: List<FeedCategory>)

  @Query("DELETE FROM FeedCategory")
  abstract fun deleteAll()

  @Transaction
  open fun deleteAndInsertCategories(feedCategories: List<FeedCategory>) {
    deleteAll()
    insert(feedCategories)
  }
}