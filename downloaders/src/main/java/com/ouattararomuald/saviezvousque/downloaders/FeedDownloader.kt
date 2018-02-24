package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Flowable

/** Provides services that allow you to download feeds. */
class FeedDownloader private constructor(private val feedService: FeedService) : FeedService {

  /** Returns the list of publications. */
  override fun getPosts(): Flowable<List<Post>> = feedService.getPosts()

  /** Returns the list of categories. */
  override fun getCategories(): Flowable<List<Category>> = feedService.getCategories()

  /**
   * Gets the post of the given [page].
   *
   * @param page The page for which you want to retrieve the list of posts.
   */
  override fun getPostByPage(page: Int): Flowable<List<Post>> = feedService.getPostByPage(page)

  /**
   * Gets the post of the given [categoryId].
   *
   * @param categoryId The category for which you want to retrieve the list of posts.
   */
  override fun getPostsByCategory(categoryId: Int): Flowable<List<Post>> = feedService.getPostsByCategory(categoryId)

  companion object Builder {
    /** Creates a new instance of [FeedDownloader]. */
    fun create(feedService: FeedService): FeedDownloader = FeedDownloader(feedService)
  }
}