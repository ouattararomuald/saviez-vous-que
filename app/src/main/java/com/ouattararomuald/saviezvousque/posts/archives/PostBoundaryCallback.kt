package com.ouattararomuald.saviezvousque.posts.archives

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PostBoundaryCallback(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : PagedList.BoundaryCallback<Post>() {

  companion object {
    internal const val PAGE_SIZE = 10
    internal const val PREFETCH_DISTANCE = 5
    private const val PREFETCH_SIZE = PAGE_SIZE * PREFETCH_DISTANCE
  }

  var requestState: MutableLiveData<RequestState> = MutableLiveData()
  var pageIndex: Int = 1

  private val disposable = CompositeDisposable()

  @MainThread
  override fun onZeroItemsLoaded() {
    disposable.add(
        feedDownloader.getPostByPage(pageIndex = 1, pageSize = PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
              updateState(RequestState.LOADING)
              savePosts(posts, R.id.archive_menu_item)
            }, {
              updateState(RequestState.ERROR)
            })
    )
  }

  @MainThread
  override fun onItemAtFrontLoaded(itemAtFront: Post) {
  }

  @MainThread
  override fun onItemAtEndLoaded(itemAtEnd: Post) {
    disposable.add(
        feedDownloader.getPostByPage(pageIndex = getNextPage(), pageSize = PREFETCH_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
              updateState(RequestState.LOADING)
              savePosts(posts, R.id.archive_menu_item)
            }, {
              updateState(RequestState.ERROR)
            })
    )
  }

  private fun getNextPage(): Int {
    return ++pageIndex
  }

  private fun updateState(state: RequestState) {
    this.requestState.postValue(state)
  }

  private fun savePosts(posts: List<Post>, categoryId: Int) {
    feedRepository.savePostsInTransaction(categoryId, posts)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }
}