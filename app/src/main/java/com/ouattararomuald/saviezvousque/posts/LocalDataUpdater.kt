package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/** Fetches data from remote source and saves them in local database. */
class LocalDataUpdater(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : CoroutineScope {

  private var downloadStateListener: DownloadStateListener? = null

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + supervisorJob

  val postsDatabaseObserver: Flow<List<PostWithCategory>>
    get() = feedRepository.postsFlow()
  val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>>
    get() = feedRepository.categoriesFlow()

  suspend fun downloadCategoriesAndPosts() {
    downloadStateListener?.onDownloadStarted()
    val categories = downloadCategories()
    downloadPostsByCategories(categories)
    downloadStateListener?.onDownloadFinished()
  }

  fun dispose() {
    disposable.dispose()
    coroutineContext.cancelChildren()
    downloadStateListener = null
  }

  fun setDownloadStateListener(listener: DownloadStateListener) {
    downloadStateListener = listener
  }

  private suspend fun downloadCategories(): List<Category> {
    val categories = feedDownloader.getCategories()
    saveCategories(categories)
    return categories
  }

  private fun saveCategories(categories: List<Category>) {
    disposable.add(feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe())
  }

  /** Download the [Post]s for all available categories. */
  private suspend fun downloadPostsByCategories(categories: List<Category>) = coroutineScope {
    categories.forEach { category ->
      launch {
        val posts = feedDownloader.getPostsByCategory(category.id)
        savePosts(posts, category.id)
      }
    }
  }

  private fun savePosts(posts: List<PostAdapter>, categoryId: Int) {
    disposable.add(feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe())
  }

  interface DownloadStateListener {
    suspend fun onDownloadStarted()

    suspend fun onDownloadFinished()
  }
}
