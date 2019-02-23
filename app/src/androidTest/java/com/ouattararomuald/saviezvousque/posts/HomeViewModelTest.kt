package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.mockito.Mock

class HomeViewModelTest {

  @Mock private lateinit var feedDownloader: FeedDownloader
  @Mock private lateinit var feedRepository: FeedRepository
  private lateinit var homeViewModel: HomeViewModel

  @Before
  fun setUp() {
  }

  @After
  fun tearDown() {
  }

  fun loadPostsByCategories() {
    homeViewModel = HomeViewModel(feedDownloader, feedRepository)
    val categoriesFlowable = mockedCategoriesObservable()
    val testSubscriber = TestSubscriber.create<List<Category>>()
    categoriesFlowable.subscribe(testSubscriber)
  }

  private fun mockedCategoriesObservable(): Flowable<List<Category>> {
    return Flowable.fromArray(listOf(
        Category(1, 1, "Category 1", "slug-1"),
        Category(2, 1, "Category 2", "slug-2")
    ))
  }
}