package com.ouattararomuald.saviezvousque.downloaders

import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader.Builder.create
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DownloaderModule {
  @Provides
  @Singleton
  fun retrofit(): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.FEED_BASE_URL)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Provides fun feedService(retrofit: Retrofit): FeedService {
    return retrofit.create(FeedService::class.java)
  }

  @Provides fun feedDownloader(feedService: FeedService): FeedDownloader = create(feedService)
}