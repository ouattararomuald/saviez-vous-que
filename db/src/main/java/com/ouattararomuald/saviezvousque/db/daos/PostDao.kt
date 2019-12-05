package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostQueries
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostDao @Inject constructor(private val postQueries: PostQueries) {

  fun getPosts(): Flow<List<PostWithCategory>> = postQueries.getPosts().asFlow().mapToList()

  fun getPostsByCategory(categoryId: Int): Flow<List<PostWithCategory>> = postQueries.getPostsByCategory(
      categoryId).asFlow().mapToList()

  fun createPosts(posts: List<Post>) {
    posts.forEach { post -> createPost(post) }
  }

  fun createPost(post: Post) {
    postQueries.createPost(post.id, post.title, post.content, post.categoryId, post.imageUrl,
        post.publishedOn, post.updatedOn)
  }
}