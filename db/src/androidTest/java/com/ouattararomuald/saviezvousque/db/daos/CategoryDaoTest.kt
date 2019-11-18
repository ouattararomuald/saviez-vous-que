package com.ouattararomuald.saviezvousque.db.daos

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ouattararomuald.saviezvousque.db.Database
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

  private lateinit var categoryDao: CategoryDao

  @Before
  fun setUp() {
    val database = getDatabase()
    categoryDao = CategoryDao(database.categoryQueries)
  }

  private fun getDatabase(): Database {
    val appContext = InstrumentationRegistry.getInstrumentation().context
    val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, appContext, "test.db")
    val localDateTimeAdapter = LocalDateTimeAdapter()
    return Database(driver, Post.Adapter(localDateTimeAdapter, localDateTimeAdapter))
  }
}