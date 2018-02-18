@file:JvmName("ConvertersUtil")

package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import org.threeten.bp.OffsetDateTime

internal fun List<Post>.toFeedItems(): List<FeedItem> = map {
  it.toFeedItem()
}

internal fun Post.toFeedItem(): FeedItem = FeedItem(
    id,
    "",
    publicationDateUtc.toOffsetDateTime(),
    lastUpdateUtc.toOffsetDateTime(),
    content.value
)

internal fun String.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(this)

internal fun List<Category>.toFeedCategories(): List<FeedCategory> = map {
  it.toFeedCategory()
}

internal fun Category.toFeedCategory(): FeedCategory = FeedCategory(
    id,
    count,
    name,
    slug
)
