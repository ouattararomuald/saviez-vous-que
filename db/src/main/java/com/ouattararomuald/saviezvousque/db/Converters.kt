package com.ouattararomuald.saviezvousque.db

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Content
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.common.Title
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal fun List<Post>.toFeedItems(categoryId: Int): List<FeedItem> = map {
  it.toFeedItem(categoryId)
}

internal fun Post.toFeedItem(categoryId: Int): FeedItem = FeedItem(
    id,
    categoryId,
    getImageUrl(),
    publicationDateUtc.toLocalDateTime(),
    lastUpdateUtc.toLocalDateTime(),
    title.value,
    content.value
)

internal fun List<FeedItem>.toPosts(categoryId: Int): List<Post> = map { it.toPost(categoryId) }

internal fun FeedItem.toPost(categoryId: Int): Post = Post(
    id,
    categoryId,
    publishedOn.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    updatedOn.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    Title(value = title),
    Content(value = content)
)

internal fun String.toLocalDateTime(): LocalDateTime {
  return LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

internal fun List<Category>.toFeedCategories(): List<FeedCategory> = map {
  it.toFeedCategory()
}

internal fun Category.toFeedCategory(): FeedCategory = FeedCategory(id, count, name, slug)

internal fun List<FeedCategory>.toCategories(): List<Category> = map {
  it.toCategory()
}

internal fun FeedCategory.toCategory(): Category = Category(id, numberOfItems, name, slug)
