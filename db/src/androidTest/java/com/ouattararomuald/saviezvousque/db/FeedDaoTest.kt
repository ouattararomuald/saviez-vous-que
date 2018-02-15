package com.ouattararomuald.saviezvousque.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.EmptyResultSetException
import android.arch.persistence.room.Room
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedDaoTest {
  companion object {
    const val FAKE_CATEGORY = "fake-category"
    const val FAKE_XML = "<fake-xml></fake-xml>"
  }

  private lateinit var database: AppDatabase
  private lateinit var feedDao: FeedDao

  // Make sure that Room executes all the database operations instantly.
  @JvmField
  @Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    createDatabase()
  }

  private fun createDatabase() {
    val appContext = InstrumentationRegistry.getTargetContext()
    database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    feedDao = database.feedDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun getAllFeed() {
    val feeds = insertFeeds(quantity = 10)

    feedDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feeds)
  }

  @Test
  fun getFeedByCategoryShouldSuccess() {
    val feed = Feed(FeedDaoTest.FAKE_CATEGORY, FeedDaoTest.FAKE_XML, 1)

    feedDao.insert(feed)

    feedDao.getFeedByCategory(category = FeedDaoTest.FAKE_CATEGORY)
        .test()
        .assertNoErrors()
        .assertComplete()
        .assertValueCount(1)
        .assertValue(feed)
  }

  @Test
  fun getFeedByCategoryShouldFails() {
    feedDao.getFeedByCategory(category = "${FeedDaoTest.FAKE_CATEGORY} 1")
        .test()
        .assertError(EmptyResultSetException::class.java)
        .assertNotComplete()
        .assertNoValues()
  }

  @Test
  fun insertFeedShouldSuccess() {
    val feed = Feed(FeedDaoTest.FAKE_CATEGORY, FeedDaoTest.FAKE_XML, id = 1)

    feedDao.insert(feed)

    feedDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(listOf(feed))
  }

  @Test(expected = SQLiteConstraintException::class)
  fun insertFeedShouldFails() {
    val feed = Feed(FeedDaoTest.FAKE_CATEGORY, FeedDaoTest.FAKE_XML, id = 1)

    feedDao.insert(feed)
    feedDao.insert(feed)
  }

  @Test
  fun deleteAllFeedsShouldSuccess() {
    val feeds = insertFeeds(quantity = 10)

    feedDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feeds)

    feedDao.deleteAll()

    feedDao.getAll()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(emptyList())
  }

  /**
   * Insert the given [quantity] of [Feed](s) into the database.
   *
   * Each feed will be saved with an id corresponding to its index + 1.
   *
   * For example:
   * ```
   * insertFeeds(quantity = 2) // insert feeds: Feed(id=1,...) and Feed(id=2,...)
   * ```
   *
   * @return a list containing the feeds inserted.
   */
  private fun insertFeeds(quantity: Int = 1): List<Feed> {
    if (quantity < 1) {
      throw IllegalArgumentException("quantity must be greater than 1")
    }

    val feeds = mutableListOf<Feed>()

    (1..quantity).forEach { index ->
      val feed = Feed("${FeedDaoTest.FAKE_CATEGORY} $index", FeedDaoTest.FAKE_XML, id = index)

      feedDao.insert(feed)

      feeds.add(feed)
    }

    return feeds
  }
}