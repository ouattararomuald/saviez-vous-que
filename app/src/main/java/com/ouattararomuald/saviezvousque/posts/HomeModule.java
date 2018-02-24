package com.ouattararomuald.saviezvousque.posts;

import android.arch.lifecycle.ViewModelProviders;
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
abstract class HomeModule {

  @Singleton
  @Provides
  static ViewModelFactory viewModelFactory(FeedDownloader feedDownloader) {
    return new ViewModelFactory(feedDownloader);
  }

  @Singleton
  @Provides
  static HomeViewModel homeViewModel(HomeActivity activity, ViewModelFactory viewModelFactory) {
    return ViewModelProviders.of(activity, viewModelFactory).get(HomeViewModel.class);
  }
}
