package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedService {
  @GET("posts/")
  fun getPosts(): Single<List<Post>>

  @GET("posts/")
  fun getPostByPage(@Query("page") page: Int): Single<List<Post>>

  @GET("categories/")
  fun getCategories(): Single<List<Category>>

  @GET("posts/")
  fun getPostsByCategory(@Query("categories") categoryId: Int): Single<List<Post>>
}