package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Completable
import io.reactivex.functions.Action
import io.reactivex.internal.operators.completable.CompletableFromAction
import javax.inject.Inject


class FeedRepository @Inject constructor(
    private val feedCategoryDao: FeedCategoryDao,
    private val feedItemDao: FeedItemDao
) {

  /**
   * Saves the given [Post]s.
   *
   * @param posts List of [Post]s to save.
   *
   * @return a [Completable].
   */
  fun savePosts(posts: List<Post>): Completable {
    return CompletableFromAction(Action {
      feedItemDao.insert(posts.toFeedItems())
    })
  }

  /**
   * Saves the given [Category](ies).
   *
   * @param categories List of [Category](ies) to save.
   *
   * @return a [Completable].
   */
  fun saveCategories(categories: List<Category>): Completable {
    return CompletableFromAction(Action {
      feedCategoryDao.insert(categories.toFeedCategories())
    })
  }
}