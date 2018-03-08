package com.ouattararomuald.saviezvousque.downloaders

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.File
import java.util.*


class FeedDownloaderTest {

  private val gson = Gson()
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
        .addConverterFactory(GsonConverterFactory.create())
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

  @Test
  fun getPostShouldSuccess() {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {
    }.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    feedDownloader.getPosts()
        .test()
        .assertNoErrors()
        .assertValue(posts)
  }

  @Test
  fun getCategoriesShouldSuccess() {
    val file = TestsUtil.loadFileFromResources(TestsUtil.CATEGORIES_JSON_FILE)
    val categoriesListType = object : TypeToken<List<Category>>() {
    }.type
    val categories: List<Category> = gson.fromJson(file.toText(), categoriesListType)

    feedDownloader.getCategories()
        .test()
        .assertNoErrors()
        .assertValue(categories)
  }

  @Test
  fun getPostByPage() {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {
    }.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    val rand = Random()

    feedDownloader.getPostByPage(rand.nextInt())
        .test()
        .assertNoErrors()
        .assertValue(posts)
  }

  @Test
  fun getPostsByCategoryShouldSuccess() {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {
    }.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    val rand = Random()

    feedDownloader.getPostsByCategory(rand.nextInt())
        .test()
        .assertNoErrors()
        .assertValue(posts)
  }

  private inner class MockFeedService(
      private val delegate: BehaviorDelegate<FeedService>
  ) : FeedService {
    private val postFile: File = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    private val categoriesFile: File = TestsUtil.loadFileFromResources(TestsUtil.CATEGORIES_JSON_FILE)

    override fun getPosts(): Single<List<Post>> {
      return delegate.returningResponse(generatePosts()).getPosts()
    }

    override fun getPostByPage(page: Int): Single<List<Post>> {
      return delegate.returningResponse(generatePosts()).getPostByPage(page)
    }

    override fun getCategories(): Single<List<Category>> {
      val categoriesListType = object : TypeToken<List<Category>>() {
      }.type
      val categories: List<Category> = gson.fromJson(categoriesFile.toText(), categoriesListType)

      return delegate.returningResponse(categories).getCategories()
    }

    override fun getPostsByCategory(categoryId: Int): Single<List<Post>> {
      return delegate.returningResponse(generatePosts()).getPostsByCategory(categoryId)
    }

    private fun generatePosts(): List<Post> {
      val postListType = object : TypeToken<List<Post>>() {
      }.type
      return gson.fromJson(postFile.toText(), postListType)
    }
  }
}