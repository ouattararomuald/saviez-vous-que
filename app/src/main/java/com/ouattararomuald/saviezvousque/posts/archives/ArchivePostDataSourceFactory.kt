package com.ouattararomuald.saviezvousque.posts.archives

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader

class ArchivePostDataSourceFactory(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : DataSource.Factory<Int, Post>() {

  companion object {
    internal const val NETWORK_PAGE_SIZE = 10
  }

  var requestState: MutableLiveData<RequestState> = MutableLiveData()

  private var dataSource: ArchivePostDataSource? = null

  override fun create(): DataSource<Int, Post> {
    dataSource = ArchivePostDataSource(feedDownloader, feedRepository, requestState)
    return dataSource!!
  }

  fun loadNextPage() {
    dataSource?.loadNextPage()
  }

  fun invalidate() {
    dataSource?.invalidate()
  }
}