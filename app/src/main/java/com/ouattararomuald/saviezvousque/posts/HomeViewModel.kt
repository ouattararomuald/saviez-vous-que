package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
  private val feedRepository: FeedRepository
) : ViewModel() {

  /** Observable list of categories */
  internal val categories: MutableLiveData<List<Category>> = MutableLiveData()

  /** Observable list of posts grouped by category */
  internal val posts: MutableLiveData<MutableMap<Int, List<Post>>> = MutableLiveData()

  private val disposable = CompositeDisposable()

  init {
    loadCategoriesFromDatabase()
    downloadCategories()

    posts.value = mutableMapOf()
  }

  fun refreshData() {
    loadCategoriesFromDatabase()
    downloadCategories()
  }

  private fun loadCategoriesFromDatabase() {
    disposable.add(
        feedRepository.getAllCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
              categories.postValue(getOrderedCategories(it.toMutableList()))
              loadPostsByCategories(it)
            }
    )
  }

  private fun downloadCategories() {
    disposable.add(
        feedDownloader.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              //categories.postValue(getOrderedCategories(it.toMutableList()))
              saveCategories(it)
              downloadPostsByCategories(it)
            }, {
              // TODO: Log error
            })
    )
  }

  private fun saveCategories(categories: List<Category>) {
    feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  /** Download the [Post]s for all available categories. */
  private fun loadPostsByCategories(categories: List<Category>) {
    categories.forEach { category ->
      disposable.add(
          feedRepository.getAllPostsByCategory(category.id)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe { posts ->
                val postMap = mutableMapOf<Int, List<Post>>()
                postMap[category.id] = posts
                postMap.putAll(this.posts.value!!)
                this.posts.value = postMap
              }
      )
    }
  }

  /** Download the [Post]s for all available categories. */
  private fun downloadPostsByCategories(categories: List<Category>) {
    categories.forEach { category ->
      disposable.add(
          feedDownloader.getPostsByCategory(category.id)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe { posts ->
                //val postMap = mutableMapOf<Int, List<Post>>()
                //postMap[category.id] = posts
                //postMap.putAll(this.posts.value!!)
                //this.posts.postValue(postMap)
                savePosts(posts, category.id)
              }
      )
    }
  }

  private fun savePosts(posts: List<Post>, categoryId: Int) {
    feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  private fun getOrderedCategories(categories: MutableList<Category>): List<Category> {
    return moveMainCategoryToTop(categories)
  }

  private fun moveMainCategoryToTop(categories: MutableList<Category>): List<Category> {
    val mainCategory = categories.findLast { category -> category.slug == "saviezvousque" }

    mainCategory?.let {
      categories.apply {
        remove(mainCategory)
        add(0, mainCategory)
      }
    }

    return categories
  }

  fun getPostsByCategory(categoryId: Int): List<Post> {
    posts.value?.let {
      if (it.containsKey(categoryId)) {
        return it[categoryId]!!
      }
    }

    return emptyList()
  }
}
