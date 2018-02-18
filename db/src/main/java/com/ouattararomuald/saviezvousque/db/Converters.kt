@file:JvmName("ConvertersUtil")

package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal fun List<Post>.toFeedItems(): List<FeedItem> = map {
  it.toFeedItem()
}

internal fun Post.toFeedItem(): FeedItem = FeedItem(
    id,
    "",
    publicationDateUtc.toLocalDateTime(),
    lastUpdateUtc.toLocalDateTime(),
    content.value
)

internal fun String.toLocalDateTime(): LocalDateTime {
  return LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

internal fun List<Category>.toFeedCategories(): List<FeedCategory> = map {
  it.toFeedCategory()
}

internal fun Category.toFeedCategory(): FeedCategory = FeedCategory(
    id,
    count,
    name,
    slug
)
