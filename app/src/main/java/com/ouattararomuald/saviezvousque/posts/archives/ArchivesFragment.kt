package com.ouattararomuald.saviezvousque.posts.archives

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import kotlinx.android.synthetic.main.fragment_archives.postsRecyclerView

class ArchivesFragment : Fragment() {

  private lateinit var feedDownloader: FeedDownloader
  private lateinit var feedRepository: FeedRepository

  private lateinit var postsAdapter: PagedPostAdapter
  private lateinit var posts: LiveData<PagedList<Post>>

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_archives, container, false)
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
    val config = PagedList.Config.Builder()
        .setPrefetchDistance(PostBoundaryCallback.PREFETCH_DISTANCE)
        .setPageSize(PostBoundaryCallback.PAGE_SIZE)
        .setEnablePlaceholders(true)
        .build()

    //val factory = ArchivePostDataSourceFactory(feedDownloader, feedRepository)
    val boundaryCallback = PostBoundaryCallback(feedDownloader, feedRepository)
    val databaseSource = feedRepository.getItems(R.id.archive_menu_item)

//    factory.requestState.observe(this, Observer<RequestState> {
//       when (it) {
//         RequestState.ERROR , RequestState.DONE -> {
//           progressBar.isGone = true
//         }
//         RequestState.LOADING -> {
//           progressBar.isVisible = true
//         }
//       }
//    })

    posts = LivePagedListBuilder(databaseSource, config)
        .setBoundaryCallback(boundaryCallback)
        .build()

    posts.observe(this, Observer<PagedList<Post>> {
      //boundaryCallback.pageIndex = it.size / PostBoundaryCallback.PAGE_SIZE
      postsAdapter.submitList(it)
    })
  }
}
