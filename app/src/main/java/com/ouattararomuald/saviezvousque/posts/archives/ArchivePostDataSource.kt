package com.ouattararomuald.saviezvousque.posts.archives

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class ArchivePostDataSource(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository,
  private val requestState: MutableLiveData<RequestState>
) : PageKeyedDataSource<Int, Post>() {

  private val disposable = CompositeDisposable()

  private var initialParams: LoadInitialParams<Int>? = null
  private var initialCallback: LoadInitialCallback<Int, Post>? = null
  private var nextPageParams: LoadParams<Int>? = null
  private var nextPageCallback: LoadCallback<Int, Post>? = null

  override fun loadInitial(
    params: LoadInitialParams<Int>,
    callback: LoadInitialCallback<Int, Post>
  ) {
    initialParams = params
    initialCallback = callback
    updateState(RequestState.LOADING)
    disposable.add(
        feedDownloader.getPostByPage(pageIndex = 1, pageSize = params.requestedLoadSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
              updateState(RequestState.DONE)
              callback.onResult(posts, 0, 2)
              //savePosts(posts, R.id.archive_menu_item)
            }, {
              updateState(RequestState.ERROR)
            })
    )
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
    nextPageParams = params
    load(params.key, params.requestedLoadSize, params.key + 1, callback)
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
    load(params.key, params.requestedLoadSize, params.key - 1, callback)
  }

  private fun load(
    pageIndex: Int,
    pageSize: Int,
    adjacentPageKey: Int,
    callback: LoadCallback<Int, Post>
  ) {
    this.nextPageCallback = callback
    updateState(RequestState.LOADING)
    disposable.add(
        feedDownloader.getPostByPage(pageIndex, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
              updateState(RequestState.DONE)
              callback.onResult(posts, adjacentPageKey)
              //savePosts(posts, R.id.archive_menu_item)
            }, {
              updateState(RequestState.ERROR)
            })
    )
  }

  private fun updateState(state: RequestState) {
    this.requestState.postValue(state)
  }

  fun loadNextPage() {
    if (nextPageParams != null && nextPageCallback != null) {
      loadAfter(nextPageParams!!, nextPageCallback!!)
    } else if (initialParams != null && initialCallback != null) {
      loadInitial(initialParams!!, initialCallback!!)
    }
  }

  private fun savePosts(posts: List<Post>, categoryId: Int) {
    feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }
}