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
import kotlinx.android.synthetic.main.paginated_post_view.view.posts_recycler_view
import kotlinx.android.synthetic.main.paginated_post_view.view.progressBar
import kotlinx.android.synthetic.main.paginated_post_view.view.viewLayout

/**
 * Displays a list of [Post]s in an infinite scroll view.
 */
class PaginatedPostView : FrameLayout {

  companion object {
    private const val TWO = 2
    private const val THREE = 3
  }

  private lateinit var feedDownloader: FeedDownloader
  private lateinit var feedRepository: FeedRepository

  private lateinit var factory: ArchivePostDataSourceFactory
  private lateinit var config: PagedList.Config

  private lateinit var postsAdapter: PagedPostAdapter
  private lateinit var posts: LiveData<PagedList<Post>>

  private var lifecycleOwner: LifecycleOwner? = null

  internal val isNotVisible: Boolean
    get() = !isVisible

  constructor(context: Context) : super(context) {
    initialize()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initialize()
  }

  private fun initialize() {
    inflate(context, R.layout.paginated_post_view, this)

    postsAdapter = PagedPostAdapter()

    posts_recycler_view.apply {
      layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
      }
      setHasFixedSize(true)
      adapter = postsAdapter
    }

    // initial page size to fetch can also be configured here too
    config = PagedList.Config.Builder()
        .setPrefetchDistance(ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
        .setPageSize(TWO * ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
        .setInitialLoadSizeHint(THREE * ArchivePostDataSourceFactory.NETWORK_PAGE_SIZE)
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
      factory = ArchivePostDataSourceFactory(feedDownloader)
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
        else -> {
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
