package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomePresenter @Inject constructor(
  private val localDataUpdater: LocalDataUpdater,
  private val view: HomeContract.View
) : HomeContract.Presenter, ViewModel(), CoroutineScope, LocalDataUpdater.DownloadStateListener {

  /** Observable list of categories. */
  internal val categories: MutableLiveData<List<CategoryIdAndName>> = MutableLiveData()
  /** Observable list of posts. */
  internal val posts: MutableLiveData<List<PostWithCategory>> = MutableLiveData()

  private val postsDatabaseObserver: Flow<List<PostWithCategory>> =
      localDataUpdater.postsDatabaseObserver
  private val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>> =
      localDataUpdater.categoriesDatabaseObserver

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + supervisorJob

  init {
    localDataUpdater.setDownloadStateListener(this)
    launch { observeCategoriesFromDatabase() }
    launch { observePostsFromDatabase() }
    launch { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override fun onDestroy() {
    localDataUpdater.dispose()
    disposable.dispose()
    coroutineContext.cancelChildren()
  }

  override fun refreshData() {
    launch { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override suspend fun onDownloadStarted() {
    withContext(Dispatchers.Main) {
      view.showProgressBar()
    }
  }

  override suspend fun onDownloadFinished() {
    withContext(Dispatchers.Main) {
      view.hideProgressBar()
    }
  }

  private suspend fun observeCategoriesFromDatabase() {
    categoriesDatabaseObserver.collect { categories.postValue(it) }
  }

  private suspend fun observePostsFromDatabase() {
    postsDatabaseObserver.collect { postsWithCategories ->
      posts.postValue(postsWithCategories)
    }
  }

  override fun getPostsByCategory(categoryId: Int): List<PostWithCategory> {
    posts.value?.let {
      return it.filter { postWithCategory -> postWithCategory.categoryId == categoryId }
    }

    return emptyList()
  }
}
