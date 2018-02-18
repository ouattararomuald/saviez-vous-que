package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedService {
  @GET("posts/")
  fun getPosts(): Flowable<List<Post>>

  @GET("posts/")
  fun getPostByPage(@Query("page") page: Int): Flowable<List<Post>>

  @GET("categories/")
  fun getCategories(): Flowable<List<Category>>

  @GET("posts/")
  fun getPostsByCategory(@Query("categories") categoryId: Int): Flowable<List<Post>>
}