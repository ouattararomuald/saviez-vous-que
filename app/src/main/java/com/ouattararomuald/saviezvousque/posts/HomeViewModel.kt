package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeViewModel @Inject constructor(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : ViewModel(), CoroutineScope {

  /** Observable list of categories. */
  internal val categories: MutableLiveData<List<Category>> = MutableLiveData()

  /** Observable list of posts grouped by category's ID. */
  internal val posts: MutableLiveData<Map<Int, List<Post>>> = MutableLiveData()

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + supervisorJob

  init {
    loadCategoriesFromDatabase()
    launch { downloadCategories() }

    posts.value = mutableMapOf()
  }

  fun refreshData() {
    loadCategoriesFromDatabase()
    launch { downloadCategories() }
  }

  private fun loadCategoriesFromDatabase() {
    disposable.add(
        feedRepository.categoryStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
              categories.postValue(getOrderedCategories(it.toMutableList()))
              loadPostsByCategories(getOrderedCategories(it.toMutableList()))
            }
    )
  }

  /** Download the [Post]s for all available categories. */
  private fun loadPostsByCategories(categories: List<Category>) {
    val singles = ArrayList<Single<List<Post>>>()

    categories.forEach { category ->
      singles.add(feedRepository.postsByCategoryStream(category.id)
          .filter { it.isNotEmpty() }
          .subscribeOn(Schedulers.io())
          .firstOrError()
      )
    }

    disposable.add(
        Single.merge(singles)
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items ->
              val postItems = mutableListOf<Post>()
              items.forEach { postItems.addAll(it) }
              postItems.sortBy { it.lastUpdateUtc }

              val postGroupedByCategory = postItems.groupBy { it.categoryId }
              val map = mutableMapOf<Int, List<Post>>()
              postGroupedByCategory.keys.forEach { k ->
                map[k] = postGroupedByCategory.getValue(k)
              }

              if (categories.isNotEmpty()) {
                map[categories.first().id] = postItems.takeLast(10)
              }

              this.posts.postValue(map)
            }
    )
  }

  private suspend fun downloadCategories() {
    val categories = feedDownloader.getCategories()
    saveCategories(categories)
    downloadPostsByCategories(categories)

    /*disposable.add(
        feedDownloader.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              saveCategories(it)
              downloadPostsByCategories(it)
            }, {
              // TODO: Log error
            })
    )*/
  }

  private fun saveCategories(categories: List<Category>) {
    feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  /** Download the [Post]s for all available categories. */
  private suspend fun downloadPostsByCategories(categories: List<Category>) = runBlocking {
    categories.forEach { category ->
      launch {
        val posts = feedDownloader.getPostsByCategory(category.id)
        savePosts(posts, category.id)
      }
      /*disposable.add(
          feedDownloader.getPostsByCategory(category.id)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe { posts ->
                savePosts(posts, category.id)
              }
      )*/
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
        return it.getValue(categoryId)
      }
    }

    return emptyList()
  }
}
