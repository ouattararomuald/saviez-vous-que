package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.DateTimeConverter
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostQueries
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter

class PostDao @Inject constructor(private val postQueries: PostQueries) {

  fun getPosts(): Flow<List<PostWithCategory>> = postQueries.getPosts().asFlow().mapToList()

  fun getPostsByCategoryObservable(categoryId: Int): Flow<List<PostWithCategory>> = postQueries.getPostsByCategory(
      categoryId).asFlow().mapToList()

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> = postQueries.getPostsByCategory(
    categoryId).executeAsList()

  fun createPosts(posts: List<PostAdapter>) {
    posts.toPosts().forEach { post -> createPost(post) }
  }

  fun createPost(post: Post) {
    if (postExists(post)) {
      postQueries.updatePostWithId(post.title, post.content, post.categoryId, post.imageUrl,
          post.publishedOn, post.updatedOn, post.id)
    } else {
      postQueries.createPost(post.id, post.title, post.content, post.categoryId, post.imageUrl,
          post.publishedOn, post.updatedOn)
    }
  }

  private fun postExists(post: Post): Boolean = postExists(post.id)

  private fun postExists(postId: Int): Boolean = postQueries.countPostWithId(postId).executeAsOne() > 0

  private fun List<PostAdapter>.toPosts(): List<Post> = map { post ->
    Post(
        post.id, post.categoryId, post.title.value, post.content.value, post.getImageUrl(),
        DateTimeConverter.toLocalDateTime(post.publicationDateUtc),
        DateTimeConverter.toLocalDateTime(post.lastUpdateUtc)
    )
  }
}