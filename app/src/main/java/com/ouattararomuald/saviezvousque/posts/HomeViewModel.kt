package com.ouattararomuald.saviezvousque.posts

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val feedDownloader: FeedDownloader) : ViewModel() {
  /** Observable list of categories */
  val categories: ObservableList<Category> = ObservableArrayList()

  private val disposable = CompositeDisposable()

  private fun downloadCategories() {
    disposable.add(
        feedDownloader.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
              categories.clear()
              categories.addAll(it)
            }
    )
  }

  fun start() {
    downloadCategories()
  }

  fun stop() {
    disposable.clear()
  }
}
