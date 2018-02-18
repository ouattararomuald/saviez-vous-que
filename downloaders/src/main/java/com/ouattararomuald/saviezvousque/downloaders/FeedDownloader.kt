package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Flowable

class FeedDownloader private constructor(private val feedService: FeedService) : FeedService {

  override fun getPosts(): Flowable<List<Post>> = feedService.getPosts()

  override fun getCategories(): Flowable<List<Category>> = feedService.getCategories()

  override fun getPostByPage(page: Int): Flowable<List<Post>> = feedService.getPostByPage(page)

  override fun getPostsByCategory(categoryId: Int): Flowable<List<Post>> = feedService.getPostsByCategory(categoryId)

  companion object Builder {
    fun create(feedService: FeedService): FeedDownloader = FeedDownloader(feedService)
  }
}