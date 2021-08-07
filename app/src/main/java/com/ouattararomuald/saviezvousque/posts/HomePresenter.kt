package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ouattararomuald.saviezvousque.core.AppCoroutineDispatchers
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class HomePresenter @Inject constructor(
  private val localDataUpdater: LocalDataUpdater,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel(), HomeContract.Presenter, LocalDataUpdater.DownloadStateListener {

  /** Observable list of categories. */
  internal val categoriesObservable: MutableLiveData<List<CategoryIdAndName>> = MutableLiveData()
  /** Observable list of posts. */
  internal val postsObservable: MutableLiveData<List<PostWithCategory>> = MutableLiveData()

  private val postsDatabaseObserver: Flow<List<PostWithCategory>> =
      localDataUpdater.postsDatabaseObserver
  private val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>> =
      localDataUpdater.categoriesDatabaseObserver

  private lateinit var view: HomeContract.View

  init {
    localDataUpdater.setDownloadStateListener(this)
    viewModelScope.launch(dispatchers.io) { observeCategoriesFromDatabase() }
    viewModelScope.launch(dispatchers.io) { observePostsFromDatabase() }
    viewModelScope.launch(dispatchers.io) { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override fun start(view: HomeContract.View) {
    this.view = view
  }

  override fun onDestroy() {
    localDataUpdater.dispose()
  }

  override fun refreshData() {
    viewModelScope.launch(dispatchers.io) { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override suspend fun onDownloadStarted() {
    withContext(dispatchers.main) {
      view.showProgressBar()
    }
  }

  override suspend fun onDownloadFinished() {
    withContext(dispatchers.main) {
      view.hideProgressBar()
    }
  }

  private suspend fun observeCategoriesFromDatabase() {
    categoriesDatabaseObserver.collect { categoriesObservable.postValue(it) }
  }

  private suspend fun observePostsFromDatabase() {
    postsDatabaseObserver.collect { postsWithCategories ->
      postsObservable.postValue(postsWithCategories)
    }
  }

  override fun getPostsByCategory(categoryId: Int): List<PostWithCategory> {
    postsObservable.value?.let {
      return it.filter { postWithCategory -> postWithCategory.categoryId == categoryId }
    }

    return emptyList()
  }
}
