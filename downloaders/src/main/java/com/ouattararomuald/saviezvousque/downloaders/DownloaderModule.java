package com.ouattararomuald.saviezvousque.downloaders;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Module
abstract class DownloaderModule {

  @Provides public static Retrofit retrofit(String baseUrl) {
    return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build();
  }

  @Provides public static FeedService feedService(Retrofit retrofit) {
    return retrofit.create(FeedService.class);
  }

  @Provides public static FeedDownloader feedDownloader(FeedService feedService) {
    return FeedDownloader.Builder.create(feedService);
  }
}
