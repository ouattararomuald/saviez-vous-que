package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedService {
  @GET("posts")
  suspend fun getPosts(): List<Post>

  @GET("posts")
  suspend fun getPostByPage(
    @Query("page") pageIndex: Int,
    @Query("per_page") pageSize: Int
  ): List<Post>

  @GET("categories")
  suspend fun getCategories(): List<Category>

  @GET("posts")
  suspend fun getPostsByCategory(@Query("categories") categoryId: Int): List<Post>
}