package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.common.Rss
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.File


class FeedDownloaderTest {

  private lateinit var feedDownloader: FeedDownloader
  private lateinit var behavior: NetworkBehavior
  private lateinit var mockRetrofit: MockRetrofit
  private lateinit var retrofit: Retrofit
  private lateinit var mockFeedService: MockFeedService

  @Before
  fun setUp() {
    retrofit = Retrofit.Builder()
        .baseUrl("http://fake.url.com")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
    behavior = NetworkBehavior.create()
    mockRetrofit = MockRetrofit.Builder(retrofit)
        .networkBehavior(behavior)
        .build()

    val delegate: BehaviorDelegate<FeedService> = mockRetrofit.create(FeedService::class.java)
    mockFeedService = MockFeedService(delegate)

    feedDownloader = FeedDownloader.create(mockFeedService)
  }

  @After
  fun tearDown() {
  }

  /**
   * Makes sure that the [FeedDownloader] can download a feed and the result can be successfully
   * parsed.
   */
  @Test
  fun downloadSuccess() {
    val file = TestsUtil.loadFileFromResources(TestsUtil.FEED_FILE_NAME)
    val expectedRss = TestsUtil.deserializeXmlFromFile(file)

    feedDownloader.download(FeedDownloader.Category.NEWS)
        .test()
        .assertNoErrors()
        .assertValue(expectedRss)
  }

  private class MockFeedService(private val delegate: BehaviorDelegate<FeedService>) : FeedService {

    private val file: File = TestsUtil.loadFileFromResources(TestsUtil.FEED_FILE_NAME)
    private val rss: Rss = TestsUtil.deserializeXmlFromFile(file)!!

    override fun downloadFeed(path: String): Flowable<Rss> {
      return delegate.returningResponse(rss).downloadFeed(path)
    }

    override fun downloadArchiveFeed(paged: Int): Flowable<Rss> {
      return delegate.returningResponse(rss).downloadArchiveFeed(paged)
    }
  }
}