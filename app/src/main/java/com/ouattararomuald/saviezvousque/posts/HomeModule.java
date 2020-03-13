package com.ouattararomuald.saviezvousque.posts;

import androidx.lifecycle.ViewModelProvider;
import com.ouattararomuald.saviezvousque.db.FeedRepository;
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
abstract class HomeModule {

  @Singleton
  @Provides
  static ViewModelFactory viewModelFactory(FeedDownloader feedDownloader,
      FeedRepository feedRepository, HomeContract.View view) {
    return new ViewModelFactory(feedDownloader, feedRepository, view);
  }

  @Singleton
  @Provides
  static HomePresenter homeViewModel(HomeActivity activity, ViewModelFactory viewModelFactory) {
    return new ViewModelProvider(activity, viewModelFactory).get(HomePresenter.class);
  }
}
