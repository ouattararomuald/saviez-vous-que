package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ouattararomuald.saviezvousque.core.AppCoroutineDispatchers
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import javax.inject.Inject

/** Factory for ViewModels */
class ViewModelFactory @Inject constructor(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository,
  private val view: HomeContract.View,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModelProvider.Factory {

  @Suppress("UnsafeCast")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HomePresenter::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return HomePresenter(LocalDataUpdater(feedDownloader, feedRepository), view, dispatchers) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
