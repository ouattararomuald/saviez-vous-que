package com.ouattararomuald.saviezvousque.posts

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import javax.inject.Inject


/** Factory for ViewModels */
class ViewModelFactory @Inject constructor(private val feedDownloader: FeedDownloader)
  : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return HomeViewModel(feedDownloader) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}