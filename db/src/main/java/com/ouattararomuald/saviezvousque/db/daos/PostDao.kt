package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostQueries
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import javax.inject.Inject

class PostDao @Inject constructor(private val postQueries: PostQueries) {

  fun getPosts(): List<PostWithCategory> = postQueries.getPosts().executeAsList()

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> = postQueries.getPostsByCategory(
      categoryId).executeAsList()

  fun createPosts(posts: List<Post>) {
    posts.forEach { post -> createPost(post) }
  }

  fun createPost(post: Post) {
    postQueries.createPost(post.id, post.title, post.content, post.categoryId, post.imageUrl,
        post.publishedOn, post.updatedOn)
  }
}