package com.ouattararomuald.saviezvousque.downloaders

import io.reactivex.Flowable

class FeedDownloader private constructor(private val feedService: FeedService) {

  fun download(category: Category): Flowable<Rss> = feedService.downloadFeed(path = category.path)

  companion object Builder {
    fun create(feedService: FeedService): FeedDownloader = FeedDownloader(feedService)
  }

  enum class Category(val path: String) {
    ADVICES("/conseils-utiles"),
    FIRST_NAMES("/prenoms"),
    LOL("/lol"),
    NEWS("/"),
    TERROR("/terreur"),
    ARCHIVES("/archives")
  }
}