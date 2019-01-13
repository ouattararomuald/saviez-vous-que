package com.ouattararomuald.saviezvousque.posts.archives

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import com.ouattararomuald.saviezvousque.posts.AbstractInternetAwareFragment
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import kotlinx.android.synthetic.main.fragment_archives.noInternetLayout
import kotlinx.android.synthetic.main.fragment_archives.postsRecyclerView
import kotlinx.android.synthetic.main.fragment_archives.progressBar
import kotlinx.android.synthetic.main.fragment_archives.viewLayout

class ArchivesFragment : AbstractInternetAwareFragment() {

  private lateinit var feedDownloader: FeedDownloader
  private lateinit var feedRepository: FeedRepository

  private lateinit var postsAdapter: PagedPostAdapter
  private lateinit var posts: LiveData<PagedList<Post>>

  private lateinit var factory: ArchivePostDataSourceFactory
  private lateinit var config: PagedList.Config

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(
        R.layout.fragment_archives,
        container,
        false
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    activity?.let {
      feedDownloader = it.getDownloaderComponent().feedDownloader()
      feedRepository = it.getDbComponent().feedRepository()
    }

    postsAdapter = PagedPostAdapter()

    postsRecyclerView.apply {
      layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
      }
      setHasFixedSize(true)
      adapter = postsAdapter
    }

    // initial page size to fetch can also be configured here too
    config = PagedList.Config.Builder()
        .setPrefetchDistance(ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
        .setPageSize(2 * ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
        .setInitialLoadSizeHint(3 * ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
        .setEnablePlaceholders(true)
        .build()

    factory = ArchivePostDataSourceFactory(feedDownloader, feedRepository)

    factory.requestState.observe(this, Observer<RequestState> {
      when (it) {
        RequestState.LOADING -> {
          progressBar.isVisible = true
        }
        RequestState.ERROR, RequestState.DONE -> {
          progressBar.isGone = true
        }
      }
    })

    observePageLoading()
  }

  private fun observePageLoading() {
    posts = LivePagedListBuilder(factory, config).build()

    posts.observe(this, Observer<PagedList<Post>> {
      postsAdapter.submitList(it)
    })
  }

  override fun onInternetAvailable() {
    observePageLoading()
    viewLayout.isVisible = true
    noInternetLayout.isGone = true
  }

  override fun onInternetLost() {
    factory.invalidate()
    viewLayout.isGone = true
    noInternetLayout.isVisible = true
  }
}
