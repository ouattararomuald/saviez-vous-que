package com.ouattararomuald.saviezvousque.downloaders

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.File
import java.util.Random
import java.util.concurrent.Executors

class FeedDownloaderTest {

  private val gson = Gson()
  private lateinit var feedDownloader: FeedDownloader
  private lateinit var behavior: NetworkBehavior
  private lateinit var mockRetrofit: MockRetrofit
  private lateinit var retrofit: Retrofit
  private lateinit var mockFeedService: MockFeedService

  private val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

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

    mockFeedService = MockFeedService()

    feedDownloader = FeedDownloader.create(mockFeedService)
    Dispatchers.setMain(mainThreadSurrogate)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    mainThreadSurrogate.close()
  }

  @Test
  fun getPost() = runBlocking {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {}.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    val downloadedPosts = feedDownloader.getPosts()
    assertThat(downloadedPosts).isEqualTo(posts)
  }

  @Test
  fun getCategories() = runBlocking {
    val file = TestsUtil.loadFileFromResources(TestsUtil.CATEGORIES_JSON_FILE)
    val categoriesListType = object : TypeToken<List<Category>>() {}.type
    val categories: List<Category> = gson.fromJson(file.toText(), categoriesListType)

    val downloadedCategories = feedDownloader.getCategories()
    assertThat(downloadedCategories).isEqualTo(categories)
  }

  @Test
  fun getPostByPage() = runBlocking {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {}.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    var downloadedPostByPage = feedDownloader.getPostByPage(pageIndex = 0, pageSize = 5)
    assertThat(downloadedPostByPage).isEqualTo(posts)

    downloadedPostByPage = feedDownloader.getPostByPage(pageIndex = 0, pageSize = 2)
    assertThat(downloadedPostByPage).isEqualTo(posts.subList(fromIndex = 0, toIndex = 2))
  }

  @Test
  fun getPostsByCategory() = runBlocking {
    val file = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    val postListType = object : TypeToken<List<Post>>() {}.type
    val posts: List<Post> = gson.fromJson(file.toText(), postListType)

    val rand = Random()

    val downloadedPostByCategory = feedDownloader.getPostsByCategory(rand.nextInt())
    assertThat(downloadedPostByCategory).isEqualTo(posts)
  }

  private inner class MockFeedService : FeedService {
    private val postFile: File = TestsUtil.loadFileFromResources(TestsUtil.POSTS_JSON_FILE)
    private val categoriesFile: File =
        TestsUtil.loadFileFromResources(TestsUtil.CATEGORIES_JSON_FILE)

    override suspend fun getPosts(): List<Post> = generatePosts()

    override suspend fun getPostByPage(pageIndex: Int, pageSize: Int): List<Post> {
      val fromIndex = pageIndex * pageSize
      val toIndexExclusive = fromIndex + pageSize
      val posts = generatePosts()

      return if (fromIndex >= 0 && toIndexExclusive <= posts.size && fromIndex <= toIndexExclusive) {
        generatePosts().subList(fromIndex, toIndexExclusive)
      } else {
        throw IllegalArgumentException() // should raise HttpException normally
      }
    }

    override suspend fun getCategories(): List<Category> {
      val categoriesListType = object : TypeToken<List<Category>>() {}.type
      return gson.fromJson(categoriesFile.toText(), categoriesListType)
    }

    override suspend fun getPostsByCategory(categoryId: Int): List<Post> {
      return generatePosts()
    }

    private fun generatePosts(): List<Post> {
      val postListType = object : TypeToken<List<Post>>() {}.type
      return gson.fromJson(postFile.toText(), postListType)
    }
  }
}