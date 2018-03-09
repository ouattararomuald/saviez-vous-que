package com.ouattararomuald.saviezvousque.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeedRepositoryTest {

  private lateinit var database: AppDatabase
  private lateinit var feedCategoryDao: FeedCategoryDao
  private lateinit var feedItemDao: FeedItemDao
  private lateinit var feedRepository: FeedRepository

  // Make sure that Room executes all the database operations instantly.
  @JvmField
  @Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  init {
    AndroidThreeTen.init(InstrumentationRegistry.getTargetContext())
  }

  @Before
  fun setUp() {
    createDatabase()
  }

  private fun createDatabase() {
    val appContext = InstrumentationRegistry.getTargetContext()
    database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    feedCategoryDao = database.feedCategoryDao()
    feedItemDao = database.feedItemDao()
    feedRepository = FeedRepository(feedCategoryDao, feedItemDao)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun savePosts() {
    val posts = TestsUtil.generatePosts(quantity = 10)
    feedRepository.savePosts(posts, categoryId = 1)
        .test()
        .assertNoErrors()
        .assertComplete()

    val feedItems = posts.toFeedItems(1)
        .sortedByDescending { it.updatedOn }
        .sortedByDescending { it.publishedOn }

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feedItems)
  }

  @Test
  fun saveCategories() {
    val categories = TestsUtil.generateCategories(quantity = 10)
    feedRepository.saveCategories(categories)
        .test()
        .assertNoErrors()
        .assertComplete()

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(categories.toFeedCategories().sortedBy { it.name })
  }
}