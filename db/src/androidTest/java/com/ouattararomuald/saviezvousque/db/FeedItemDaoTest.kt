package com.ouattararomuald.saviezvousque.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime

@RunWith(AndroidJUnit4::class)
class FeedItemDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var feedItemDao: FeedItemDao

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
    feedItemDao = database.feedItemDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun getAllFeedItems() {
    val feedItems = insertFeedItems(quantity = 10)

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feedItems)
  }

  @Test
  fun insertFeedItemShouldSuccess() {
    val feedItem = createFeedItem()

    feedItemDao.insert(listOf(feedItem))

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(listOf(feedItem))
  }

  @Test
  fun insertDuplicateFeedItemShouldReplaceExistingOne() {
    val feed = createFeedItem()

    feedItemDao.insert(listOf(feed))
    feedItemDao.insert(listOf(feed))

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(listOf(feed))
  }

  @Test
  fun deleteAllFeedItemsShouldSuccess() {
    val feeds = insertFeedItems(quantity = 10)

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feeds)

    feedItemDao.deleteAll()

    feedItemDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(emptyList())
  }

  private fun createFeedItem(id: Int = 1): FeedItem = FeedItem(
      id,
      "htt://fake-url-$id.jpeg",
      LocalDateTime.now(),
      LocalDateTime.now(),
      "content"
  )

  /**
   * Insert the given [quantity] of [FeedItem](s) into the database.
   *
   * Each feed will be saved with an id corresponding to its index + 1.
   *
   * For example:
   * ```
   * insertFeedItems(quantity = 2) // insert feeds: FeedItem(id=1,...) and FeedItem(id=2,...)
   * ```
   *
   * @return a list containing the feeds inserted.
   */
  private fun insertFeedItems(quantity: Int = 1): List<FeedItem> {
    val feedItems = TestsUtil.generateFeedItems(quantity)
    feedItemDao.insert(feedItems)
    return feedItems
  }
}