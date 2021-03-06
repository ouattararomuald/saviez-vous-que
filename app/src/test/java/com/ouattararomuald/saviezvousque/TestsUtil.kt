package com.ouattararomuald.saviezvousque

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Content
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.common.Title
import com.ouattararomuald.saviezvousque.db.FeedCategory
import com.ouattararomuald.saviezvousque.db.FeedItem
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import org.threeten.bp.LocalDateTime

class TestsUtil {

  companion object {

    @JvmStatic
    fun generateFeedCategories(quantity: Int = 1): List<FeedCategory> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val feedCategories = mutableListOf<FeedCategory>()

      repeat(quantity) { index ->
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
            index,
            "htt://fake-url-$index.jpeg",
            date,
            date.plusMinutes(5),
            "title",
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

      repeat(quantity) { index ->
        val category = Category(id = index, count = 10, name = "name $index", slug = "slug")

        categories.add(category)
      }

      return categories
    }

    @JvmStatic
    fun generatePosts(quantity: Int = 1): List<PostWithCategory> {
      if (quantity < 1) {
        throw IllegalArgumentException("quantity must be greater or equals to 1")
      }

      val posts = mutableListOf<PostWithCategory>()

      repeat(quantity) { index ->
        /*val post = Post(
            id = index,
            categoryId = index,
            publicationDateUtc = "2018-02-17T10:36:22",
            lastUpdateUtc = "2018-02-18T11:36:22",
            title = Title(value = "title"),
            content = Content(value = "<p>")
        )*/

        val post = object: PostWithCategory {
          override val postId: Int
            get() = index
          override val title: String
            get() = "title"
          override val content: String
            get() = "<p>"
          override val imageUrl: String?
            get() = "http://fake.image.com"
          override val categoryId: Int
            get() = index
          override val categoryName: String
            get() = "Category $index"
          override val categorySlug: String
            get() = "Category slug $index"
          override val publishedOn: LocalDateTime
            get() = LocalDateTime.now()
          override val updatedOn: LocalDateTime
            get() = LocalDateTime.now()
        }

        posts.add(post)
      }

      return posts
    }
  }
}