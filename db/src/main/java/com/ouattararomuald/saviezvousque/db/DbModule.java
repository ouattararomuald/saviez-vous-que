package com.ouattararomuald.saviezvousque.db;

import android.content.Context;
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter;
import com.ouattararomuald.saviezvousque.db.daos.CategoryDao;
import com.ouattararomuald.saviezvousque.db.daos.PostDao;
import com.squareup.sqldelight.android.AndroidSqliteDriver;
import com.squareup.sqldelight.db.SqlDriver;
import dagger.Module;
import dagger.Provides;

@Module
abstract class DbModule {

  @Provides
  static PostQueries postQueries(Database database) {
    return database.getPostQueries();
  }

  @Provides
  static CategoryQueries categoryQueries(Database database) {
    return database.getCategoryQueries();
  }

  @Provides
  static Database sqlDelightDatabase(SqlDriver driver, LocalDateTimeAdapter localDateTimeAdapter) {
    return Database.Companion.invoke(driver,
        new Post.Adapter(localDateTimeAdapter, localDateTimeAdapter));
  }

  @Provides
  static LocalDateTimeAdapter localDateTimeAdapter() {
    return new LocalDateTimeAdapter();
  }

  @Provides
  static SqlDriver sqlDriver(Context context, String databaseName) {
    return new AndroidSqliteDriver(Database.Companion.getSchema(), context, databaseName);
  }

  @Provides
  static CategoryDao categoryDao(CategoryQueries categoryQueries) {
    return new CategoryDao(categoryQueries);
  }

  @Provides
  static PostDao postDao(PostQueries postQueries) {
    return new PostDao(postQueries);
  }

  @Provides
  public static FeedRepository feedRepository(
      CategoryDao categoryDao,
      PostDao postDao
  ) {
    return new FeedRepository(categoryDao, postDao);
  }
}
