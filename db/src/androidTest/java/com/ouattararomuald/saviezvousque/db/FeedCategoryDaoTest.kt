package com.ouattararomuald.saviezvousque.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedCategoryDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var feedCategoryDao: FeedCategoryDao

  // Make sure that Room executes all the database operations instantly.
  @JvmField
  @Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    createDatabase()
  }

  private fun createDatabase() {
    val appContext = InstrumentationRegistry.getInstrumentation().context
    database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    feedCategoryDao = database.feedCategoryDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun getAllCategories() {
    val feedCategories = insertFeedCategories(quantity = 10)

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feedCategories)
  }

  @Test
  fun insertFeedCategoryShouldSuccess() {
    val feedCategory = createFeedCategory()

    feedCategoryDao.insert(listOf(feedCategory))

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(listOf(feedCategory))
  }

  @Test
  fun insertDuplicateFeedCategoryShouldReplaceExistingOne() {
    val feedCategory = createFeedCategory()

    feedCategoryDao.insert(listOf(feedCategory))
    feedCategoryDao.insert(listOf(feedCategory))

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(listOf(feedCategory))
  }

  @Test
  fun deleteAllFeedCategoriesShouldSuccess() {
    val feedCategories = insertFeedCategories(quantity = 10)

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feedCategories)

    feedCategoryDao.deleteAll()

    feedCategoryDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(emptyList())
  }

  private fun createFeedCategory(id: Int = 1): FeedCategory = FeedCategory(
      id,
      id,
      "name $id",
      "slug $id"
  )

  /**
   * Insert the given [quantity] of [FeedCategory](s) into the database.
   *
   * Each feed will be saved with an id corresponding to its index + 1.
   *
   * For example:
   * ```
   * insertFeeds(quantity = 2) // insert feeds: FeedCategory(id=1,...) and FeedCategory(id=2,...)
   * ```
   *
   * @return a list containing the categories inserted.
   */
  private fun insertFeedCategories(quantity: Int = 1): List<FeedCategory> {
    val feedCategories = TestsUtil.generateFeedCategories(quantity)
    feedCategoryDao.insert(feedCategories)
    return feedCategories
  }
}