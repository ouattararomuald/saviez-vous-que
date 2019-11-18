package com.ouattararomuald.saviezvousque.db;

import android.content.Context;
import androidx.room.Room;
import com.ouattararomuald.saviezvousque.db.daos.FeedCategoryDao;
import com.ouattararomuald.saviezvousque.db.daos.FeedItemDao;
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
