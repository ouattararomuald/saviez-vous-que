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
        .build();
  }

  @Provides
  public static FeedCategoryDao feedCategoryDao(AppDatabase database) {
    return database.feedCategoryDao();
  }

  @Provides
  public static FeedItemDao feedItemDao(AppDatabase database) {
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
