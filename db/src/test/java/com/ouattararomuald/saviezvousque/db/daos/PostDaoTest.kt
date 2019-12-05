package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Database
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
    val posts = listOf(createPost(id = 1, categoryId = 1), createPost(id = 2, categoryId = 1), createPost(id = 3, categoryId = 2))

    postDao.createPosts(posts)

    postDao.getPostsByCategory(categoryId = 1)
    postDao.getPostsByCategory(categoryId = 2)

    Mockito.verify(postQueries, Mockito.times(1)).getPostsByCategory(categoryId = 1)
    Mockito.verify(postQueries, Mockito.times(1)).getPostsByCategory(categoryId = 2)
  }

  @Test
  fun `createPosts() should delegate to createPost()`() {
    val posts = listOf(createPost(id = 1, categoryId = 1), createPost(id = 2, categoryId = 1), createPost(id = 3, categoryId = 2))

    val spyPostDao = spy(postDao)
    spyPostDao.createPosts(posts)

    Mockito.verify(spyPostDao, Mockito.times(1)).createPosts(posts)
    posts.forEach { post ->
      Mockito.verify(spyPostDao, Mockito.times(1)).createPost(post)
    }
  }

  private fun createPost(id: Int, categoryId: Int): Post {
    return object : Post {
      override val id: Int
        get() = id
      override val categoryId: Int
        get() = categoryId
      override val title: String
        get() = "Title $id"
      override val content: String
        get() = "Content $id"
      override val imageUrl: String?
        get() = "http://server.fake.com/image-$id.png"
      override val publishedOn: LocalDateTime
        get() = LocalDateTime.now()
      override val updatedOn: LocalDateTime
        get() = LocalDateTime.now()
    }
  }
}