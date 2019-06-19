package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post

/** Provides services that allow you to download feeds. */
class FeedDownloader private constructor(private val feedService: FeedService) : FeedService {

  /** Returns the list of publications. */
  override suspend fun getPosts(): List<Post> = feedService.getPosts()

  /** Returns the list of categories. */
  override suspend fun getCategories(): List<Category> = feedService.getCategories()

  /**
   * Gets the post of the page at the given [pageIndex].
   *
   * @param pageIndex The page for which you want to retrieve the list of posts.
   * @param pageSize The number of items to contained in the page.
   */
  override suspend fun getPostByPage(
    pageIndex: Int,
    pageSize: Int
  ): List<Post> = feedService.getPostByPage(pageIndex, pageSize)

  /**
   * Gets the post of the given [categoryId].
   *
   * @param categoryId The category for which you want to retrieve the list of posts.
   */
  override suspend fun getPostsByCategory(categoryId: Int): List<Post> {
    return feedService.getPostsByCategory(categoryId)
  }

  companion object Builder {
    /** Creates a new instance of [FeedDownloader]. */
    fun create(feedService: FeedService): FeedDownloader = FeedDownloader(feedService)
  }
}