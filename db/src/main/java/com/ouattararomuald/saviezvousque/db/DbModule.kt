package com.ouattararomuald.saviezvousque.db

import android.content.Context
import com.ouattararomuald.saviezvousque.db.Database.Companion.Schema
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
  @Singleton
  @Provides
  fun localDateTimeAdapter(): LocalDateTimeAdapter = LocalDateTimeAdapter()

  @Singleton
  @Provides
  fun sqlDriver(@ApplicationContext context: Context): SqlDriver = AndroidSqliteDriver(
    Schema,
    context,
    BuildConfig.DATABASE_NAME
  )

  @Singleton
  @Provides
  fun sqlDelightDatabase(
    driver: SqlDriver,
    localDateTimeAdapter: LocalDateTimeAdapter
  ): Database = Database(
    driver,
    Post.Adapter(
      localDateTimeAdapter,
      localDateTimeAdapter
    )
  )

  @Provides fun postQueries(database: Database): PostQueries = database.postQueries

  @Provides fun categoryQueries(database: Database): CategoryQueries = database.categoryQueries
}