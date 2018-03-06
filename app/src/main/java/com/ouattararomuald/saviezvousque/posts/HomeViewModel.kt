package com.ouattararomuald.saviezvousque.posts

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val feedDownloader: FeedDownloader,
    private val feedRepository: FeedRepository,
    private val viewContract: ViewContract
) : ViewModel() {

  /** Observable list of categories */
  private val categories: ObservableList<Category> = ObservableArrayList()

  /** Observable list of displayedPosts */
  val displayedPosts: ObservableList<Post> = ObservableArrayList()

  /** Observable list of displayedPosts grouped by category. */
  private val postsByCategory: MutableMap<Int, List<Post>> = mutableMapOf()

  private val disposable = CompositeDisposable()

  fun onResume() {
    downloadCategories()
  }

  private fun downloadCategories() {
    disposable.add(
        feedDownloader.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
              categories.clear()
              categories.addAll(it)
              viewContract.onCategoriesDownloaded(it)
              displayedPosts.clear()
              downloadPosts()
            }
    )
  }

  private fun downloadPosts() {
    categories.forEach { category ->
      disposable.add(
          feedDownloader.getPostsByCategory(category.id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe { posts ->
                postsByCategory[category.id] = posts
              }
      )
    }
  }

  fun onPause() {
    disposable.clear()
  }

  fun onCategorySelected(categoryId: Int) {
    if (postsByCategory.contains(categoryId)) {
      displayedPosts.clear()
      displayedPosts.addAll(postsByCategory[categoryId]!!)
      viewContract.notifyDatasetChanged()
    }
  }
}
