package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter

class HomeViewModel @Inject constructor(
  private val feedDownloader: FeedDownloader,
  private val feedRepository: FeedRepository
) : ViewModel(), CoroutineScope {

  /** Observable list of categories. */
  internal val categories: MutableLiveData<List<CategoryIdAndName>> = MutableLiveData()

  /** Observable list of posts grouped by category ID. */
  //internal val posts: MutableLiveData<Map<Int, List<PostWithCategory>>> = MutableLiveData()
  /** Observable list of posts. */
  internal val posts: MutableLiveData<List<PostWithCategory>> = MutableLiveData()

  private val postsDatabaseObserver: Flow<List<PostWithCategory>> = feedRepository.postsFlow()
  private val categoriesDatabaseObserver: Flow<List<CategoryIdAndName>> = feedRepository.categoriesFlow()

  private val disposable = CompositeDisposable()

  private val supervisorJob = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + supervisorJob

  init {
    launch { observeCategoriesFromDatabase() }
    launch { observePostsFromDatabase() }
    launch { downloadCategories() }
  }

  fun onDestroy() {
    disposable.dispose()
    coroutineContext.cancelChildren()
  }

  fun refreshData() {
    /*launch { observeCategoriesFromDatabase() }
    launch { observePostsFromDatabase() }*/
    launch { downloadCategories() }
  }

  private suspend fun observeCategoriesFromDatabase() {
    categoriesDatabaseObserver.collect { categories.postValue(it) }
  }

  private suspend fun observePostsFromDatabase() {
    postsDatabaseObserver.collect { postsWithCategories ->
      posts.postValue(postsWithCategories)
      /*val postsGroupedByCategory = postsWithCategories.groupBy { it.categoryId }
      val map = mutableMapOf<Int, List<PostWithCategory>>()
      postsGroupedByCategory.keys.forEach { k ->
        map[k] = postsGroupedByCategory.getValue(k)
      }

      posts.postValue(map)*/
    }
  }


  /** Download the [Post]s for all available categories. */
  private fun loadPostsByCategories(categories: List<Category>) {

    /*val singles = ArrayList<Single<List<PostAdapter>>>()

    categories.forEach { category ->
      singles.add(feedRepository.feedItemsByCategoryStream(category.id)
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
              val postItems = mutableListOf<PostAdapter>()
              items.forEach { postItems.addAll(it) }
              postItems.sortBy { it.lastUpdateUtc }

              val postGroupedByCategory = postItems.groupBy { it.categoryId }
              val map = mutableMapOf<Int, List<PostAdapter>>()
              postGroupedByCategory.keys.forEach { k ->
                map[k] = postGroupedByCategory.getValue(k)
              }

              if (categories.isNotEmpty()) {
                map[categories.first().id] = postItems.takeLast(10)
              }

              TODO("type check")
              //this.posts.postValue(map)
            }
    )*/
  }

  private suspend fun downloadCategories() {
    val categories = feedDownloader.getCategories()
    saveCategories(categories)
    downloadPostsByCategories(categories)
  }

  private fun saveCategories(categories: List<Category>) {
    feedRepository.saveCategories(categories)
        .subscribeOn(Schedulers.io())
        .subscribe()
  }

  /** Download the [Post]s for all available categories. */
  private suspend fun downloadPostsByCategories(categories: List<Category>) = coroutineScope {
    categories.forEach { category ->
      launch {
        val posts = feedDownloader.getPostsByCategory(category.id)
        savePosts(posts, category.id)
      }
    }
  }

  private fun savePosts(posts: List<PostAdapter>, categoryId: Int) {
    feedRepository.savePosts(posts, categoryId)
        .subscribeOn(Schedulers.io())
        .subscribe()
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

  fun getPostsByCategory(categoryId: Int): List<PostWithCategory> {
    posts.value?.let {
      /*if (it.containsKey(categoryId)) {
        return it.getValue(categoryId)
      }*/
      return it.filter { postWithCategory ->  postWithCategory.categoryId == categoryId }
    }

    return emptyList()
  }
}
