package com.ouattararomuald.saviezvousque.db

import org.threeten.bp.OffsetDateTime

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

      var date: OffsetDateTime = OffsetDateTime.now()

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
  }
}