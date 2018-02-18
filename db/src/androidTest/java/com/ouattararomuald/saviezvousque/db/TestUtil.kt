package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Content
import com.ouattararomuald.saviezvousque.common.Post
import org.threeten.bp.LocalDateTime

class TestUtil {

  companion object {

    @JvmStatic
    fun generateFeedCategories(quantity: Int = 1): List<FeedCategory> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val feedCategories = mutableListOf<FeedCategory>()

      (1..quantity).forEach { index ->
        val feedCategory = FeedCategory(
            index,
            index,
            "name $index",
            "slug $index"
        )

        feedCategories.add(feedCategory)
      }

      return feedCategories.sortedBy { it.name }
    }

    @JvmStatic
    fun generateFeedItems(quantity: Int = 1): List<FeedItem> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val feeds = mutableListOf<FeedItem>()

      var date: LocalDateTime = LocalDateTime.now()

      (quantity until 0).forEach { index ->
        val feed = FeedItem(
            index,
            "htt://fake-url-$index.jpeg",
            date,
            date.plusMinutes(5),
            "content"
        )

        date = date.plusHours(1)

        feeds.add(feed)
      }

      return feeds.sortedByDescending { it.updatedOn }.sortedByDescending { it.publishedOn }
    }

    @JvmStatic
    fun generateCategories(quantity: Int = 1): List<Category> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val categories = mutableListOf<Category>()

      (1..quantity).forEach { index ->
        val category = Category(id = index, count = 10, name = "name $index", slug = "slug")

        categories.add(category)
      }

      return categories
    }

    @JvmStatic
    fun generatePosts(quantity: Int = 1): List<Post> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val posts = mutableListOf<Post>()

      (1..quantity).forEach { index ->
        val post = Post(
            id = index,
            publicationDateUtc = "2018-02-17T10:36:22",
            lastUpdateUtc = "2018-02-18T11:36:22",
            content = Content(value = "value $index")
        )

        posts.add(post)
      }

      return posts
    }
  }
}