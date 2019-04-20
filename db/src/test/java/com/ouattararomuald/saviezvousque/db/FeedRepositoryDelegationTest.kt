package com.ouattararomuald.saviezvousque.db

import io.reactivex.internal.operators.flowable.FlowableFromArray
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class FeedRepositoryDelegationTest {

  private lateinit var feedCategoryDao: FeedCategoryDao
  private lateinit var feedItemDao: FeedItemDao
  private lateinit var feedRepository: FeedRepository

  @Before
  fun setUp() {
    feedCategoryDao = mock(FeedCategoryDao::class.java)
    feedItemDao = mock(FeedItemDao::class.java)
    feedRepository = FeedRepository(feedCategoryDao, feedItemDao)
  }

  @After
  fun tearDown() {
  }

  @Test
  fun allCategoriesDelegateToCategoriesDao() {
    given(feedCategoryDao.getAll()).willReturn(FlowableFromArray(emptyArray()))

    feedRepository.categoryStream()

    verify(feedCategoryDao, times(1)).getAll()
  }

  @Test
  fun allPostsByCategoryDelegateToFeedItemDao() {
    given(feedItemDao.getAllByCategory(categoryId = 1)).willReturn(FlowableFromArray(emptyArray()))

    feedRepository.postsByCategoryStream(categoryId = 1)

    verify(feedItemDao, times(1)).getAllByCategory(categoryId = 1)
  }

  @Test
  fun saveDelegateToFeedItemDao() {
    val posts = Utils.generatePosts(quantity = 10)
    feedRepository.savePosts(posts, categoryId = 1).subscribe()

    verify(feedItemDao, times(1)).insert(posts.toFeedItems(categoryId = 1))
  }

  @Test
  fun saveDelegateToCategoriesDao() {
    val categories = Utils.generateCategories(quantity = 10)
    feedRepository.saveCategories(categories).subscribe()

    verify(feedCategoryDao, times(1)).insert(categories.toFeedCategories())
  }
}