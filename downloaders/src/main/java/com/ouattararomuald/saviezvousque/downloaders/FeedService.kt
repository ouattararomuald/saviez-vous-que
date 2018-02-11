package com.ouattararomuald.saviezvousque.downloaders

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
  @GET("{category}/feed/")
  fun downloadFeed(@Path("path") path: String): Flowable<Rss>

  @GET("/feed/")
  fun downloadArchiveFeed(@Query("paged") paged: Int): Flowable<Rss>
}