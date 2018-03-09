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

  private var displayedCategoryId: Int = -1

  private var isObservingDatabasePosts = false

  fun onResume() {
    observeCategoriesFromDatabase()
    downloadCategories()
  }

  private fun observeCategoriesFromDatabase() {
    feedRepository.getAllCategories()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe {
          updateCategories(it)
          askViewToRefreshCategories()
          observePostsFromDatabase()
        }
  }

  private fun updateCategories(categories: List<Category>) {
    this.categories.clear()
    this.categories.addAll(categories)
  }

  private fun askViewToRefreshCategories() {
    moveMainCategoryToTop()
    viewContract.onCategoriesDownloaded(categories)
  }

  private fun moveMainCategoryToTop() {
    // Finding the main category this way is bad but we keep it to move fast.
    // Solution is welcome.

    val mainCategory = categories.findLast { category ->
      category.slug == "saviezvousque"
    }

    mainCategory?.let {
      categories.remove(mainCategory)
      categories.add(0, mainCategory)
    }
  }

  private fun observePostsFromDatabase() {
    categories.forEach { category ->
      disposable.add(
          feedRepository.getAllPostsByCategory(category.id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe { posts ->
                postsByCategory[category.id] = posts
                if (category.id == displayedCategoryId) {
                  synchronizeDisplayedPostsWithUi(displayedCategoryId)
                }
              }
      )
      isObservingDatabasePosts = true
    }
  }

  private fun downloadCategories() {
    disposable.add(
        feedDownloader.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
              saveFetchedCategories(it)
              downloadPosts(it)
            }, {
              // TODO: Log error
            })
    )
  }

  private fun saveFetchedCategories(categories: List<Category>) {
    feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  /** Download the [Post]s for all available categories. */
  private fun downloadPosts(categories: List<Category>) {
    categories.forEach { category ->
      disposable.add(
          feedDownloader.getPostsByCategory(category.id)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe { posts ->
                saveFetchedPosts(posts, category.id)
              }
      )
    }
  }

  private fun saveFetchedPosts(posts: List<Post>, categoryId: Int) {
    if (!isObservingDatabasePosts) {
      observePostsFromDatabase()
    }

    feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  /** Notifies this [ViewModel] that the UI is being paused. */
  fun onPause() {
    disposable.clear()
  }

  /** Notifies this [ViewModel] that the [Category] with the given id has been selected. */
  fun onCategorySelected(categoryId: Int) {
    displayedCategoryId = categoryId
    synchronizeDisplayedPostsWithUi(displayedCategoryId)
  }

  private fun synchronizeDisplayedPostsWithUi(displayedCategoryId: Int) {
    if (postsByCategory.contains(displayedCategoryId)) {
      displayedPosts.clear()
      displayedPosts.addAll(postsByCategory[displayedCategoryId]!!)
      viewContract.notifyDatasetChanged()
    }
  }
}
