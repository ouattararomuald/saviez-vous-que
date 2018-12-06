package com.ouattararomuald.saviezvousque.posts.archives

import androidx.paging.DataSource
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader

class ArchivePostDataSourceFactory(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : DataSource.Factory<Int, Post>() {

  override fun create(): DataSource<Int, Post> {
    return ArchivePostDataSource(feedDownloader, feedRepository)
  }
}