package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Database
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostQueries
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.spy

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
}