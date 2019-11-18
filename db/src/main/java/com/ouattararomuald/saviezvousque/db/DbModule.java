package com.ouattararomuald.saviezvousque.db;

import android.content.Context;
import androidx.room.Room;
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter;
import com.ouattararomuald.saviezvousque.db.daos.FeedCategoryDao;
import com.ouattararomuald.saviezvousque.db.daos.FeedItemDao;
import com.squareup.sqldelight.android.AndroidSqliteDriver;
import com.squareup.sqldelight.db.SqlDriver;
import dagger.Module;
import dagger.Provides;

@Module
abstract class DbModule {

  @Provides
  public static AppDatabase database(Context context, String databaseName) {
    return Room.databaseBuilder(context, AppDatabase.class, databaseName)
        //.addMigrations(MigrationsUtil.getMIGRATION_2_3())
        //.fallbackToDestructiveMigration()
        .build();
  }

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
    return Database.Companion.invoke(driver, new Post.Adapter(localDateTimeAdapter, localDateTimeAdapter));
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
  static FeedCategoryDao feedCategoryDao(AppDatabase database) {
    return database.feedCategoryDao();
  }

  @Provides
  static FeedItemDao feedItemDao(AppDatabase database) {
    return database.feedItemDao();
  }

  @Provides
  public static FeedRepository feedRepository(
      FeedCategoryDao feedCategoryDao,
      FeedItemDao feedItemDao
  ) {
    return new FeedRepository(feedCategoryDao, feedItemDao);
  }
}
