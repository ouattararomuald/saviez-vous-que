package com.ouattararomuald.saviezvousque.db

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ouattararomuald.saviezvousque.db.daos.FeedItemDao
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime

class FeedItemDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var feedItemDao: FeedItemDao

  // Make sure that Room executes all the database operations instantly.
  @JvmField
  @Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  init {
    AndroidThreeTen.init(InstrumentationRegistry.getInstrumentation().context)
  }

  @Before
  fun setUp() {
    createDatabase()
  }

  private fun createDatabase() {
    val appContext = InstrumentationRegistry.getInstrumentation().context
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

    feedItemDao.feedItemsStream()
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

    feedItemDao.feedItemsStream()
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

    feedItemDao.feedItemsStream()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(listOf(feed))
  }

  @Test
  fun deleteAllFeedItemsShouldSuccess() {
    val feeds = insertFeedItems(quantity = 10)

    feedItemDao.feedItemsStream()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(feeds)

    feedItemDao.deleteAll()

    feedItemDao.feedItemsStream()
        .test()
        .assertNoErrors()
        .assertNotComplete()
        .assertValue(emptyList())
  }

  private fun createFeedItem(id: Int = 1): FeedItem = FeedItem(
      id,
      id,
      "htt://fake-url-$id.jpeg",
      LocalDateTime.now(),
      LocalDateTime.now(),
      "title",
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