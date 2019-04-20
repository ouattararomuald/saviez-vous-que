package com.ouattararomuald.saviezvousque.posts.archives

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import kotlinx.android.synthetic.main.paginated_post_view.view.noInternetLayout
import kotlinx.android.synthetic.main.paginated_post_view.view.postsRecyclerView
import kotlinx.android.synthetic.main.paginated_post_view.view.progressBar
import kotlinx.android.synthetic.main.paginated_post_view.view.viewLayout

class PaginatedPostView : FrameLayout {

  private lateinit var feedDownloader: FeedDownloader
  private lateinit var feedRepository: FeedRepository

  private lateinit var factory: ArchivePostDataSourceFactory
  private lateinit var config: PagedList.Config

  private lateinit var postsAdapter: PagedPostAdapter
  private lateinit var posts: LiveData<PagedList<Post>>

  private var lifecycleOwner: LifecycleOwner? = null

  constructor(context: Context) : super(context) {
    initialize()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initialize()
  }

  private fun initialize() {
    inflate(context, R.layout.paginated_post_view, this)

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
  }

  fun notifyInternetAvailable() {
    lifecycleOwner?.let { observePageLoading(it) }
    viewLayout.isVisible = true
    noInternetLayout.isVisible = false
    if (::factory.isInitialized) {
      factory.loadNextPage()
    }
  }

  fun notifyInternetLost() {
    if (::posts.isInitialized && posts.value!!.isEmpty()) {
      viewLayout.isVisible = false
      noInternetLayout.isVisible = true
    }
  }

  fun configureDataSourceFactory(
    feedDownloader: FeedDownloader,
    feedRepository: FeedRepository,
    lifecycleOwner: LifecycleOwner
  ) {
    if (!::factory.isInitialized) {
      this.feedDownloader = feedDownloader
      this.feedRepository = feedRepository
      this.lifecycleOwner = lifecycleOwner
      factory = ArchivePostDataSourceFactory(feedDownloader, feedRepository)
      posts = LivePagedListBuilder(factory, config).build()
    }

    factory.requestState.observe(lifecycleOwner, Observer<RequestState> {
      when (it) {
        RequestState.LOADING -> {
          progressBar.isVisible = true
        }
        RequestState.ERROR, RequestState.DONE -> {
          progressBar.isGone = true
        }
      }
    })

    observePageLoading(lifecycleOwner)
  }

  private fun observePageLoading(lifecycleOwner: LifecycleOwner) {
    posts.observe(lifecycleOwner, Observer<PagedList<Post>> {
      postsAdapter.submitList(it)
    })
  }
}