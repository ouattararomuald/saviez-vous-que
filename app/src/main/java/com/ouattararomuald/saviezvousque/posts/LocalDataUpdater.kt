package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.customviews.LoadingState
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter

/** Fetches data from remote source and saves them in local database. */
class LocalDataUpdater @Inject constructor(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : ViewModel() {

  private val disposable = CompositeDisposable()

  val postsDatabaseObserver: Flow<List<PostWithCategory>>
    get() = feedRepository.postsFlow()

  val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>>
    get() = feedRepository.categoriesFlow()

  private val _loadingObservable: MutableStateFlow<LoadingState> =
    MutableStateFlow(LoadingState.FINISHED)

  /** Observable loading state. */
  val loadingStateObservable: StateFlow<LoadingState> = _loadingObservable.asStateFlow()

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> = feedRepository.getPostsByCategory(categoryId)

  suspend fun downloadCategoriesAndPosts() {
    _loadingObservable.value = LoadingState.LOADING
    val categories = downloadCategories()
    downloadPostsByCategories(categories)
    _loadingObservable.value = LoadingState.FINISHED
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
      viewModelScope.launch {
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

  override fun onCleared() {
    super.onCleared()
    disposable.dispose()
  }

  interface DownloadStateListener {
    suspend fun onDownloadStarted()

    suspend fun onDownloadFinished()
  }
}
