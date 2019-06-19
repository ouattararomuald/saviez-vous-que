package com.ouattararomuald.saviezvousque.posts.archives

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class ArchivePostDataSource(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository,
  private val requestState: MutableLiveData<RequestState>
) : PageKeyedDataSource<Int, Post>(), CoroutineScope {

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + supervisorJob

  private val mutex = Mutex()

  private var initialParams: LoadInitialParams<Int>? = null
  private var initialCallback: LoadInitialCallback<Int, Post>? = null
  private var nextPageParams: LoadParams<Int>? = null
  private var nextPageCallback: LoadCallback<Int, Post>? = null

  override fun loadInitial(
    params: LoadInitialParams<Int>,
    callback: LoadInitialCallback<Int, Post>
  ) = runBlocking {
    initialParams = params
    initialCallback = callback
    updateState(RequestState.LOADING)

    try {
      withContext(Dispatchers.IO) { getPostByPage(params, callback) }
    } catch (ex: Exception) {
      updateState(RequestState.ERROR)
    }

//    disposable.add(
//        feedDownloader.getPostByPage(pageIndex = 1, pageSize = params.requestedLoadSize)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ posts ->
//              updateState(RequestState.DONE)
//              callback.onResult(posts, 0, 2)
//              //savePosts(posts, R.id.archive_menu_item)
//            }, {
//              updateState(RequestState.ERROR)
//            })
//    )
  }

  private suspend fun getPostByPage(
    params: LoadInitialParams<Int>,
    callback: LoadInitialCallback<Int, Post>
  ) {
    val posts = feedDownloader.getPostByPage(pageIndex = 1, pageSize = params.requestedLoadSize)
    updateState(RequestState.DONE)
    callback.onResult(posts, 0, 2)
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) = runBlocking {
    nextPageParams = params
    withContext(Dispatchers.IO) {
      load(params.key, params.requestedLoadSize, params.key + 1, callback)
    }
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) = runBlocking {
    withContext(Dispatchers.IO) {
      load(params.key, params.requestedLoadSize, params.key - 1, callback)
    }
  }

  private suspend fun load(
    pageIndex: Int,
    pageSize: Int,
    adjacentPageKey: Int,
    callback: LoadCallback<Int, Post>
  ) {
    nextPageCallback = callback
    updateState(RequestState.LOADING)

    try {
      withContext(Dispatchers.IO) {
        val posts = feedDownloader.getPostByPage(pageIndex, pageSize)
        updateState(RequestState.DONE)
        callback.onResult(posts, adjacentPageKey)
      }
    } catch (ex: Exception) {
      updateState(RequestState.ERROR)
    }
//    disposable.add(
//        feedDownloader.getPostByPage(pageIndex, pageSize)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ posts ->
//              updateState(RequestState.DONE)
//              callback.onResult(posts, adjacentPageKey)
//              //savePosts(posts, R.id.archive_menu_item)
//            }, {
//              updateState(RequestState.ERROR)
//            })
//    )
  }

  private suspend fun updateState(state: RequestState) {
    mutex.withLock { requestState.postValue(state) }
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