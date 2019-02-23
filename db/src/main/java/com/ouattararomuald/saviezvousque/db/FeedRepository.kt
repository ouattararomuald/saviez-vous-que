package com.ouattararomuald.saviezvousque.db

import androidx.paging.DataSource
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.Action
import io.reactivex.internal.operators.completable.CompletableFromAction
import javax.inject.Inject

class FeedRepository @Inject constructor(
  private val feedCategoryDao: FeedCategoryDao,
  private val feedItemDao: FeedItemDao
) {

  fun getAllCategories(): Flowable<List<Category>> {
    return feedCategoryDao.getAll()
        .map { feedCategories ->
          return@map feedCategories.toCategories()
        }
  }

  fun deleteAllCategories(): Completable {
    return CompletableFromAction(Action {
      feedCategoryDao.deleteAll()
    })
  }

  fun getItems(categoryId: Int): DataSource.Factory<Int, Post> = feedItemDao.getItems(
      categoryId
  ).map { feedItem -> feedItem.toPost(categoryId) }

  fun getAllPosts(): Flowable<List<Post>> {
    return feedItemDao.getAll()
        .map { feedItems ->
          return@map feedItems.toPosts(feedItems.first().categoryId)
        }
  }

  /** Returns the [Post]s of that belongs to the [Category] with the given [categoryId]. */
  fun getAllPostsByCategory(categoryId: Int): Flowable<List<Post>> {
    return feedItemDao.getAllByCategory(categoryId)
        .map { feedItems ->
          return@map feedItems.toPosts(categoryId)
        }
  }

  fun deleteAllPosts(): Completable {
    return CompletableFromAction(Action {
      feedItemDao.deleteAll()
    })
  }

  fun savePostsInTransaction(
    categoryId: Int,
    itemsToDelete: List<Int>,
    posts: List<Post>
  ): Completable {
    return CompletableFromAction(Action {
      feedItemDao.addPosts(categoryId, itemsToDelete, posts.toFeedItems(categoryId))
    })
  }

  /**
   * Saves the given [Post]s.
   *
   * @param posts List of [Post]s to save.
   * @param categoryId ID of the category associated with the given [Post]s.
   *
   * @return a [Completable].
   */
  fun savePosts(posts: List<Post>, categoryId: Int): Completable {
    return CompletableFromAction(Action {
      feedItemDao.insert(posts.toFeedItems(categoryId))
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