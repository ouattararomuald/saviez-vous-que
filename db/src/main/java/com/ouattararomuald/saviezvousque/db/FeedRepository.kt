package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.daos.CategoryDao
import com.ouattararomuald.saviezvousque.db.daos.PostDao
import io.reactivex.Completable
import io.reactivex.internal.operators.completable.CompletableFromAction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedRepository @Inject constructor(
  private val categoryDao: CategoryDao,
  private val postDao: PostDao
) {

  fun categoriesFlow(): Flow<List<CategoryIdAndName>> = categoryDao.getCategories()

  fun postsFlow(): Flow<List<PostWithCategory>> = postDao.getPosts()

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> = postDao.getPostsByCategory(categoryId)

  /**
   * Saves the given [Post]s.
   *
   * @param posts List of [Post]s to save.
   * @param categoryId ID of the category associated with the given [Post]s.
   *
   * @return a [Completable].
   */
  fun savePosts(posts: List<Post>, categoryId: Int): Completable {
    return CompletableFromAction {
      val postsWithCategory = posts.map { it.copy(categoryId = categoryId) }
      postDao.createPosts(postsWithCategory)
    }
  }

  /**
   * Saves the given [Category](ies).
   *
   * @param categories List of [Category](ies) to save.
   *
   * @return a [Completable].
   */
  fun saveCategories(categories: List<Category>): Completable {
    return CompletableFromAction {
      categoryDao.deleteCategories()
      categoryDao.createCategories(categories)
    }
  }
}