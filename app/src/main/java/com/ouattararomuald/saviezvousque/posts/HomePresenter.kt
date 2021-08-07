package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ouattararomuald.saviezvousque.core.AppCoroutineDispatchers
import com.ouattararomuald.saviezvousque.customviews.LoadingState
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePresenter @Inject constructor(
  private val localDataUpdater: LocalDataUpdater,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel(), HomeContract.Presenter, LocalDataUpdater.DownloadStateListener {

  private val _loadingStateObservable: MutableStateFlow<LoadingState> =
    MutableStateFlow(LoadingState.FINISHED)

  /** Observable loading state. */
  internal val loadingStateObservable: StateFlow<LoadingState> = localDataUpdater.loadingStateObservable

  private val _postsByCategoryObservable: MutableStateFlow<List<PostWithCategory>> =
    MutableStateFlow(emptyList())

  /** Observable list of posts by categories. */
  internal val postsByCategoryObservable: Flow<List<PostWithCategory>> =
    _postsByCategoryObservable.asStateFlow()

  /** Observable list of posts. */
  internal val postsObservable: Flow<List<PostWithCategory>> =
    localDataUpdater.postsDatabaseObserver

  /** Observable list of categories. */
  internal val categoriesObservable: Flow<List<CategoryIdAndName>> =
    localDataUpdater.categoriesDatabaseObserver

  init {
    viewModelScope.launch(dispatchers.io) { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override fun refreshData() {
    viewModelScope.launch(dispatchers.io) { localDataUpdater.downloadCategoriesAndPosts() }
  }

  override suspend fun onDownloadStarted() {
    _loadingStateObservable.value = LoadingState.LOADING
  }

  override suspend fun onDownloadFinished() {
    _loadingStateObservable.value = LoadingState.FINISHED
  }

  override fun categoryClicked(categoryId: Int) {
    viewModelScope.launch(dispatchers.io) {
      val posts = localDataUpdater.getPostsByCategory(categoryId)
      _postsByCategoryObservable.value = posts
    }
  }
}
