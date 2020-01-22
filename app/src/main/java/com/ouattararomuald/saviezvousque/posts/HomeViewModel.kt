package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter

class HomeViewModel @Inject constructor(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : ViewModel(), CoroutineScope {

  /** Observable list of categories. */
  internal val categories: MutableLiveData<List<CategoryIdAndName>> = MutableLiveData()
  /** Observable list of posts. */
  internal val posts: MutableLiveData<List<PostWithCategory>> = MutableLiveData()

  private val postsDatabaseObserver: Flow<List<PostWithCategory>> = feedRepository.postsFlow()
  private val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>> = feedRepository.categoriesFlow()

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + supervisorJob

  init {
    launch { observeCategoriesFromDatabase() }
    launch { observePostsFromDatabase() }
    launch { downloadData() }
  }

  fun onDestroy() {
    disposable.dispose()
    coroutineContext.cancelChildren()
  }

  fun refreshData() {
    launch { downloadData() }
  }

  private suspend fun observeCategoriesFromDatabase() {
    categoriesDatabaseObserver.collect { categories.postValue(it) }
  }

  private suspend fun observePostsFromDatabase() {
    postsDatabaseObserver.collect { postsWithCategories ->
      posts.postValue(postsWithCategories)
    }
  }

  /** Download categories then download posts for each category. */
  private suspend fun downloadData() {
    val categories = downloadCategories()
    downloadPostsByCategories(categories)
  }

  private suspend fun downloadCategories(): List<Category> {
    val categories = feedDownloader.getCategories()
    saveCategories(categories)
    return categories
  }

  private fun saveCategories(categories: List<Category>) {
    feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe()
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
    feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> {
    posts.value?.let {
      return it.filter { postWithCategory ->  postWithCategory.categoryId == categoryId }
    }

    return emptyList()
  }
}
