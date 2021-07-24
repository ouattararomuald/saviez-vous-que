package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.common.Content
import com.ouattararomuald.saviezvousque.common.Title
import com.ouattararomuald.saviezvousque.db.Database
import com.ouattararomuald.saviezvousque.db.DateTimeConverter
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostQueries
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.threeten.bp.LocalDateTime
import com.ouattararomuald.saviezvousque.common.Post as PostAdapter

class PostDaoTest {

  private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  private val dateAdapter = LocalDateTimeAdapter()

  private lateinit var postQueries: PostQueries
  private lateinit var postDao: PostDao

  @Before
  fun setUp() {
    Database.Schema.create(driver)
    val database = Database(driver, Post.Adapter(dateAdapter, dateAdapter))

    postQueries = spy(database.postQueries)
    postDao = PostDao(postQueries)
  }

  @After
  fun tearDown() {
  }

  @Test
  fun `verify calls for getPosts()`() {
    postDao.getPosts()

    Mockito.verify(postQueries, Mockito.times(1)).getPosts()
  }

  @Test
  fun `verify calls for getPostsByCategory()`() {
    val posts = listOf(createPost(id = 1, categoryId = 1), createPost(id = 2, categoryId = 1),
        createPost(id = 3, categoryId = 2))

    postDao.createPosts(posts.map { it.toAdapter() })

    postDao.getPostsByCategory(categoryId = 1)
    postDao.getPostsByCategory(categoryId = 2)

    Mockito.verify(postQueries, Mockito.times(1)).getPostsByCategory(categoryId = 1)
    Mockito.verify(postQueries, Mockito.times(1)).getPostsByCategory(categoryId = 2)
  }

  @Test
  fun `createPosts() should delegate to createPost()`() {
    val posts = listOf(createPost(id = 1, categoryId = 1), createPost(id = 2, categoryId = 1),
        createPost(id = 3, categoryId = 2))
    val postAdapters = posts.map { it.toAdapter() }

    val spyPostDao = spy(postDao)
    spyPostDao.createPosts(postAdapters)

    Mockito.verify(spyPostDao, Mockito.times(1)).createPosts(postAdapters)
    postAdapters.forEach { post ->
      Mockito.verify(spyPostDao, Mockito.times(1)).createPost(post.toImplementation())
    }
  }

  private fun createPost(id: Int, categoryId: Int): Post {
    return Post(
      id= id,
      categoryId = categoryId,
      title = "Title $id",
      content = "Content $id",
      imageUrl ="http://server.fake.com/image-$id.png",
      publishedOn = LocalDateTime.now(),
      updatedOn = LocalDateTime.now()
    )
  }

  private fun Post.toAdapter(): PostAdapter {
    return PostAdapter(
        this.id,
        this.categoryId,
        DateTimeConverter.fromLocalDateTime(this.publishedOn),
        DateTimeConverter.fromLocalDateTime(this.updatedOn),
        Title(this.title),
        Content(this.content)
    )
  }

  private fun PostAdapter.toImplementation(): Post {
    return Post(
        this.id, this.categoryId, this.title.value, this.content.value, this.getImageUrl(),
        DateTimeConverter.toLocalDateTime(this.publicationDateUtc),
        DateTimeConverter.toLocalDateTime(this.lastUpdateUtc)
    )
  }
}