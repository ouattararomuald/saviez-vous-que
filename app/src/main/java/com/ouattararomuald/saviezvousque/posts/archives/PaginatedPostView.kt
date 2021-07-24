package com.ouattararomuald.saviezvousque.posts.archives

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PaginatedPostViewBinding
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader

/**
 * Displays a list of [Post]s in an infinite scroll view.
 */
class PaginatedPostView : FrameLayout {

  companion object {
    private const val TWO = 2
    private const val THREE = 3
  }

  private lateinit var binding: PaginatedPostViewBinding

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
    binding = PaginatedPostViewBinding.inflate(LayoutInflater.from(context), this)

    postsAdapter = PagedPostAdapter()

    binding.postsRecyclerView.apply {
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
    binding.viewLayout.isVisible = true
    binding.noInternetLayout.isVisible = false
    if (::factory.isInitialized) {
      factory.loadNextPage()
    }
  }

  fun notifyInternetLost() {
    if (::posts.isInitialized && posts.value!!.isEmpty()) {
      binding.viewLayout.isVisible = false
      binding.noInternetLayout.isVisible = true
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

    factory.requestState.observe(lifecycleOwner) {
      when (it) {
        RequestState.LOADING -> {
          binding.progressBar.isVisible = true
        }
        RequestState.ERROR, RequestState.DONE -> {
          binding.progressBar.isGone = true
        }
        else -> {
          binding.progressBar.isGone = true
        }
      }
    }

    observePageLoading(lifecycleOwner)
  }

  private fun observePageLoading(lifecycleOwner: LifecycleOwner) {
    posts.observe(lifecycleOwner) {
      postsAdapter.submitList(it)
    }
  }
}
