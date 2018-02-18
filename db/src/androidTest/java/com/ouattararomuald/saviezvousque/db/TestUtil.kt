package com.ouattararomuald.saviezvousque.db

import org.threeten.bp.OffsetDateTime

class TestUtil {

  companion object {

    @JvmStatic
    fun gnerateFeedCategories(quantity: Int = 1): List<FeedCategory> {
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

      (1..quantity).forEach { index ->
        val feed = FeedItem(
            index,
            "htt://fake-url-$index.jpeg",
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "content"
        )

        feeds.add(feed)
      }

      return feeds
    }
  }
}