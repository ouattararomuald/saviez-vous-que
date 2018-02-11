package com.ouattararomuald.saviezvousque.downloaders

import io.reactivex.Flowable

class FeedDownloader private constructor(private val feedService: FeedService) {

  fun download(category: Category): Flowable<Rss> = feedService.downloadFeed(path = category.path)

  /**
   * Communicates response of a download request. Only one method will be invoked in response to
   * a given request.
   *
   * @see [download]
   */
  interface Callback {
    /**
     * Invoked when the download of the feed has failed.
     *
     * @param throwable Cause of the exception.
     */
    fun onDownloadFailure(throwable: Throwable)

    /**
     * Invoked when the feed has been successfully downloaded.
     *
     * @param isSuccessful true if the download finished with success.
     * @param rss The [Rss] feed that has been downloaded. If isSuccessful is false, then it will
     *  be null. Otherwise it is not null.
     */
    fun onDownloadResponse(isSuccessful: Boolean, rss: Rss?)
  }

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