package com.ouattararomuald.saviezvousque.posts;

import androidx.lifecycle.ViewModelProviders;
import com.ouattararomuald.saviezvousque.db.FeedRepository;
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
abstract class HomeModule {

  @Singleton
  @Provides
  static ViewModelFactory viewModelFactory(
      FeedDownloader feedDownloader, FeedRepository feedRepository
  ) {
    return new ViewModelFactory(feedDownloader, feedRepository);
  }

  @Singleton
  @Provides
  static HomeViewModel homeViewModel(HomeActivity activity, ViewModelFactory viewModelFactory) {
    return ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
  }
}
